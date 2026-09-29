package lk.jiat.techmart.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.producer.JMSResources;
import lk.jiat.techmart.store.TechMartStore;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = JMSResources.NOTIFICATION_QUEUE_NAME)
})
public class NotificationMDB implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @Override
    public void onMessage(Message message) {
        try {
            String text = message.getBody(String.class);
            LOGGER.info("Notification message received: " + text);
            metricsRemote.incrementCounter("MDB Messages Consumed");
            String[] parts = text == null ? new String[0] : text.split(":", 3);
            if (parts.length == 3 && "NOTIFY".equals(parts[0])) {
                Long orderId = Long.parseLong(parts[1]);
                String username = parts[2];
                TechMartStore.saveNotification(orderId, username, "EMAIL", "Order confirmation queued for #" + orderId, "SENT");
                metricsRemote.incrementCounter("Notifications Sent");
            } else {
                metricsRemote.incrementCounter("Notification Failures");
            }
        } catch (Exception e) {
            metricsRemote.incrementCounter("MDB Failures");
            throw new RuntimeException(e);
        }
    }
}
