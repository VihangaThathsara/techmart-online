package lk.jiat.techmart.bean;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.producer.JMSProducerBean;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.store.TechMartStore;
import lk.jiat.techmart.util.AppConstants;
import lk.jiat.techmart.util.TimerUtil;

import java.util.concurrent.Future;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class AsyncOrderProcessorBean {

    @EJB
    private MetricsRemote metricsRemote;

    @EJB
    private JMSProducerBean jmsProducerBean;

    @Override
    public String toString() {
        return "AsyncOrderProcessorBean";
    }

    @Asynchronous
    public Future<String> processOrderAsync(Long orderId) {
        long start = TimerUtil.start();
        try {
            OrderDTO order = TechMartStore.findOrder(orderId);
            if (order == null) {
                metricsRemote.incrementCounter("Orders Failed");
                return new AsyncResult<>("Order not found: " + orderId);
            }

            TechMartStore.updateOrderStatus(orderId, AppConstants.ORDER_STATUS_PROCESSING);
            metricsRemote.incrementCounter("Async Tasks Started");

            try {
                Thread.sleep(120L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                TechMartStore.updateOrderStatus(orderId, AppConstants.ORDER_STATUS_FAILED);
                metricsRemote.incrementCounter("Orders Failed");
                return new AsyncResult<>("Order processing interrupted: " + orderId);
            }

            TechMartStore.updateOrderStatus(orderId, AppConstants.ORDER_STATUS_COMPLETED);
            long elapsed = TimerUtil.elapsedMillis(start);
            metricsRemote.incrementCounter("Orders Completed");
            metricsRemote.incrementCounter("Async Tasks Completed");
            metricsRemote.recordMetric("Last Order Processing Time", String.valueOf(elapsed), "ms");
            TechMartStore.recordTimedMetric("Order Processing Time", elapsed);
            jmsProducerBean.publishOrderEvent(AppConstants.EVENT_ORDER_COMPLETED, orderId,
                    "Order #" + orderId + " completed in " + elapsed + " ms");
            return new AsyncResult<>("Order processed: " + orderId);
        } catch (RuntimeException e) {
            TechMartStore.updateOrderStatus(orderId, AppConstants.ORDER_STATUS_FAILED);
            metricsRemote.incrementCounter("Orders Failed");
            metricsRemote.recordMetric("Last Order Failure", e.getMessage(), "text");
            return new AsyncResult<>("Order failed: " + orderId + " - " + e.getMessage());
        }
    }
}
