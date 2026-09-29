package lk.jiat.techmart.producer;

import lk.jiat.techmart.config.EnterpriseResourceConfig;

public final class JMSResources {
    public static final String ORDER_QUEUE_NAME = EnterpriseResourceConfig.ORDER_QUEUE_PHYSICAL;
    public static final String NOTIFICATION_QUEUE_NAME = EnterpriseResourceConfig.NOTIFICATION_QUEUE_PHYSICAL;
    public static final String EVENT_TOPIC_NAME = EnterpriseResourceConfig.EVENT_TOPIC_PHYSICAL;

    public static final String ORDER_QUEUE_JNDI = EnterpriseResourceConfig.ORDER_QUEUE_JNDI;
    public static final String NOTIFICATION_QUEUE_JNDI = EnterpriseResourceConfig.NOTIFICATION_QUEUE_JNDI;
    public static final String EVENT_TOPIC_JNDI = EnterpriseResourceConfig.EVENT_TOPIC_JNDI;

    private JMSResources() {
    }
}
