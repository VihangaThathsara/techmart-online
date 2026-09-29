package lk.jiat.techmart.config;

public final class EnterpriseResourceConfig {
    public static final String JDBC_RESOURCE = "jdbc/TechMartDS";

    public static final String ORDER_QUEUE_JNDI = "jms/TechMartOrderQueue";
    public static final String NOTIFICATION_QUEUE_JNDI = "jms/TechMartNotificationQueue";
    public static final String EVENT_TOPIC_JNDI = "jms/TechMartEventTopic";

    public static final String ORDER_QUEUE_PHYSICAL = "TechMartOrderQueue";
    public static final String NOTIFICATION_QUEUE_PHYSICAL = "TechMartNotificationQueue";
    public static final String EVENT_TOPIC_PHYSICAL = "TechMartEventTopic";

    private EnterpriseResourceConfig() {
    }
}
