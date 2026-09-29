package lk.jiat.techmart.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.producer.JMSResources;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = JMSResources.EVENT_TOPIC_NAME)
})
public class AdminAlertTopicMDB implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(AdminAlertTopicMDB.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @Override
    public void onMessage(Message message) {
        try {
            String text = message.getBody(String.class);
            LOGGER.info("Admin alert topic event received: " + text);
            metricsRemote.incrementCounter("Admin Alerts Received");
            if (text != null && text.contains("LOW_STOCK")) {
                metricsRemote.recordMetric("Latest Stock Alert", text, "alert");
            }
        } catch (Exception e) {
            metricsRemote.incrementCounter("Admin Alert Failures");
            throw new RuntimeException(e);
        }
    }
}
