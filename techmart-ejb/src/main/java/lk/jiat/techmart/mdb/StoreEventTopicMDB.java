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
public class StoreEventTopicMDB implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(StoreEventTopicMDB.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @Override
    public void onMessage(Message message) {
        try {
            String text = message.getBody(String.class);
            LOGGER.info("Store topic event received: " + text);
            metricsRemote.incrementCounter("Topic Events Consumed");
            metricsRemote.recordMetric("Latest Store Event", text, "event");
        } catch (Exception e) {
            metricsRemote.incrementCounter("Topic Event Failures");
            throw new RuntimeException(e);
        }
    }
}
