package lk.jiat.techmart.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lk.jiat.techmart.bean.AsyncOrderProcessorBean;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.producer.JMSResources;
import lk.jiat.techmart.store.TechMartStore;
import lk.jiat.techmart.util.AppConstants;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = JMSResources.ORDER_QUEUE_NAME)
})
public class OrderProcessingMDB implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(OrderProcessingMDB.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @EJB
    private AsyncOrderProcessorBean asyncOrderProcessorBean;

    @Override
    public void onMessage(Message message) {
        try {
            String text = message.getBody(String.class);
            LOGGER.info("Order processing message received: " + text);
            metricsRemote.incrementCounter("MDB Messages Consumed");

            Long orderId = parseOrderId(text);
            Future<String> result = asyncOrderProcessorBean.processOrderAsync(orderId);
            try {
                result.get(4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                metricsRemote.incrementCounter("Async Timeouts");
            }
        } catch (Exception e) {
            metricsRemote.incrementCounter("MDB Failures");
            Long failedOrderId = safeParseOrderId(message);
            if (failedOrderId != null) {
                TechMartStore.updateOrderStatus(failedOrderId, AppConstants.ORDER_STATUS_FAILED);
            }
            throw new RuntimeException(e);
        }
    }

    private Long parseOrderId(String text) {
        if (text == null || !text.startsWith("ORDER:")) {
            throw new IllegalArgumentException("Invalid order message: " + text);
        }
        return Long.parseLong(text.substring("ORDER:".length()).trim());
    }

    private Long safeParseOrderId(Message message) {
        try {
            return parseOrderId(message.getBody(String.class));
        } catch (JMSException | RuntimeException ignored) {
            return null;
        }
    }
}
