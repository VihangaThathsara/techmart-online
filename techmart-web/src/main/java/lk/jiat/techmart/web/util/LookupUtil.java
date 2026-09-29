package lk.jiat.techmart.web.util;

import lk.jiat.techmart.remote.CartRemote;
import lk.jiat.techmart.remote.InventoryRemote;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.remote.UserRemote;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class LookupUtil {
    private static final String APP = "techmart-modernization";
    private static final String MODULE = "techmart-ejb";

    private LookupUtil() {
    }

    public static UserRemote userRemote() throws NamingException {
        return lookup("UserSessionBean", UserRemote.class);
    }

    public static ProductRemote productRemote() throws NamingException {
        return lookup("ProductSessionBean", ProductRemote.class);
    }

    public static CartRemote cartRemote() throws NamingException {
        return lookup("CartSessionBean", CartRemote.class);
    }

    public static OrderRemote orderRemote() throws NamingException {
        return lookup("OrderSessionBean", OrderRemote.class);
    }

    public static InventoryRemote inventoryRemote() throws NamingException {
        return lookup("InventorySessionBean", InventoryRemote.class);
    }

    public static MetricsRemote metricsRemote() throws NamingException {
        return lookup("MetricsSingletonBean", MetricsRemote.class);
    }

    private static <T> T lookup(String beanName, Class<T> type) throws NamingException {
        InitialContext ic = new InitialContext();
        String fullName = "java:global/" + APP + "/" + MODULE + "/" + beanName + "!" + type.getName();
        return type.cast(ic.lookup(fullName));
    }
}
