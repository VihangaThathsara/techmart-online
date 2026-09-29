package lk.jiat.techmart.util;

public final class AppConstants {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static final String ORDER_STATUS_RECEIVED = "RECEIVED";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_FAILED = "FAILED";

    public static final String EVENT_ORDER_RECEIVED = "ORDER_RECEIVED";
    public static final String EVENT_ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String EVENT_LOW_STOCK = "LOW_STOCK";
    public static final String EVENT_INVENTORY_SYNCED = "INVENTORY_SYNCED";

    private AppConstants() {
    }
}
