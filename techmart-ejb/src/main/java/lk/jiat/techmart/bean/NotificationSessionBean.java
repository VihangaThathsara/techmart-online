package lk.jiat.techmart.bean;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.remote.NotificationRemote;
import lk.jiat.techmart.store.TechMartStore;

import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class NotificationSessionBean implements NotificationRemote {
    private static final Logger LOGGER = Logger.getLogger(NotificationSessionBean.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @Override
    @Asynchronous
    public Future<String> sendOrderNotificationAsync(OrderDTO order) {
        try {
            LOGGER.info("Async notification prepared for order " + order.getId());
            TechMartStore.saveNotification(order.getId(), order.getUsername(), "EMAIL",
                    "Order confirmation prepared for order #" + order.getId(), "QUEUED");
            metricsRemote.incrementCounter("Async Tasks Completed");
            metricsRemote.incrementCounter("Notifications Queued");
            return new AsyncResult<>("Notification queued for order " + order.getId());
        } catch (RuntimeException e) {
            metricsRemote.incrementCounter("Notification Failures");
            return new AsyncResult<>("Notification failed for order " + order.getId() + ": " + e.getMessage());
        }
    }
}
