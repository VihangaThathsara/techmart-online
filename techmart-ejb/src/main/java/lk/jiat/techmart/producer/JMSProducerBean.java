package lk.jiat.techmart.producer;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.Topic;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.remote.MetricsRemote;

import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class JMSProducerBean {
    private static final Logger LOGGER = Logger.getLogger(JMSProducerBean.class.getName());
    private static final long MESSAGE_TTL_MILLIS = 300000L;

    @Inject
    private JMSContext jmsContext;

    @EJB
    private MetricsRemote metricsRemote;

    public void sendOrderMessage(OrderDTO order) {
        if (order == null || order.getId() == null) {
            metricsRemote.incrementCounter("JMS Send Failures");
            return;
        }
        try {
            Queue orderQueue = jmsContext.createQueue(JMSResources.ORDER_QUEUE_NAME);
            jmsContext.createProducer()
                    .setDeliveryMode(DeliveryMode.PERSISTENT)
                    .setTimeToLive(MESSAGE_TTL_MILLIS)
                    .send(orderQueue, "ORDER:" + order.getId());
            metricsRemote.incrementCounter("JMS Messages Sent");
            LOGGER.info("Order JMS message sent for order " + order.getId());
        } catch (RuntimeException e) {
            metricsRemote.incrementCounter("JMS Send Failures");
            LOGGER.log(Level.WARNING, "Order JMS message could not be sent: " + e.getMessage(), e);
        }
    }

    public void sendNotificationMessage(OrderDTO order) {
        if (order == null || order.getId() == null || order.getUsername() == null) {
            metricsRemote.incrementCounter("JMS Send Failures");
            return;
        }
        try {
            Queue notificationQueue = jmsContext.createQueue(JMSResources.NOTIFICATION_QUEUE_NAME);
            jmsContext.createProducer()
                    .setDeliveryMode(DeliveryMode.PERSISTENT)
                    .setTimeToLive(MESSAGE_TTL_MILLIS)
                    .send(notificationQueue, "NOTIFY:" + order.getId() + ":" + order.getUsername());
            metricsRemote.incrementCounter("JMS Messages Sent");
            LOGGER.info("Notification JMS message sent for order " + order.getId());
        } catch (RuntimeException e) {
            metricsRemote.incrementCounter("JMS Send Failures");
            LOGGER.log(Level.WARNING, "Notification JMS message could not be sent: " + e.getMessage(), e);
        }
    }

    public void publishOrderEvent(String eventType, Long orderId, String message) {
        publishTopicEvent(eventType + ":ORDER:" + orderId + ":" + message);
    }

    public void publishInventoryEvent(String eventType, Long productId, String message) {
        publishTopicEvent(eventType + ":PRODUCT:" + productId + ":" + message);
    }

    private void publishTopicEvent(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            metricsRemote.incrementCounter("JMS Topic Failures");
            return;
        }
        try {
            Topic eventTopic = jmsContext.createTopic(JMSResources.EVENT_TOPIC_NAME);
            jmsContext.createProducer()
                    .setDeliveryMode(DeliveryMode.PERSISTENT)
                    .send(eventTopic, payload);
            metricsRemote.incrementCounter("JMS Topic Events");
            LOGGER.info("Topic event published: " + payload);
        } catch (RuntimeException e) {
            metricsRemote.incrementCounter("JMS Topic Failures");
            LOGGER.log(Level.WARNING, "Topic event could not be published: " + e.getMessage(), e);
        }
    }
}
