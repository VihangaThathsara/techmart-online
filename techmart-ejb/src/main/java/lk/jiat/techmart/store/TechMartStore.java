package lk.jiat.techmart.store;

import lk.jiat.techmart.dto.MetricDTO;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.UserDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;
import lk.jiat.techmart.util.AppConstants;
import lk.jiat.techmart.util.PasswordHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public final class TechMartStore {
    private static final Map<String, UserDTO> USERS = new ConcurrentHashMap<>();
    private static final Map<Long, ProductDTO> PRODUCTS = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, WarehouseStockDTO>> WAREHOUSE_INVENTORY = new ConcurrentHashMap<>();
    private static final List<OrderDTO> ORDERS = new CopyOnWriteArrayList<>();
    private static final Map<String, MetricDTO> METRICS = new ConcurrentHashMap<>();
    private static final Map<String, Long> TIMER_TOTALS = new ConcurrentHashMap<>();
    private static final Map<String, Long> TIMER_COUNTS = new ConcurrentHashMap<>();
    private static final AtomicLong ORDER_ID = new AtomicLong(1000);
    private static final Set<String> MONITORED_METRICS = new HashSet<>(Arrays.asList(
            "Database Status", "Application Starts", "Orders Received", "Orders Completed", "Orders Failed",
            "JMS Messages Sent", "JMS Send Failures", "JMS Topic Events", "JMS Topic Failures",
            "MDB Messages Consumed", "MDB Failures", "Topic Events Consumed", "Topic Event Failures",
            "Admin Alerts Received", "Admin Alert Failures",
            "Async Tasks Started", "Async Tasks Completed", "Async Timeouts",
            "Inventory Reservations", "Inventory Reservation Failures", "Inventory Releases", "Inventory Sync Runs",
            "Notifications Queued", "Notifications Sent", "Notifications Stored", "Notification Failures",
            "Database Write Failures", "HTTP Requests", "EJB Invocations",
            "Average Order Processing Time", "Last Order Processing Time",
            "Average Order Placement Time", "Last Order Placement Time",
            "Average Product Catalogue Lookup Time", "Last Product Catalogue Lookup Time",
            "Average Inventory Reservation Time", "Last Inventory Reservation Time",
            "Last Order Time", "Last HTTP Time", "Last Order Failure",
            "Latest Store Event", "Latest Stock Alert"
    ));

    private static boolean isMetricTracked(String name) {
        return name != null && MONITORED_METRICS.contains(name);
    }

    static {
        seed();
    }

    private TechMartStore() {
    }

    private static void seed() {
        USERS.put("admin", new UserDTO(1L, "admin", PasswordHash.hash("1234"), "TechMart Admin", "admin@techmart.lk", AppConstants.ROLE_ADMIN));
        USERS.put("vihanga", new UserDTO(2L, "vihanga", PasswordHash.hash("1234"), "Vihanga", "vihanga@techmart.lk", AppConstants.ROLE_USER));

        addProduct(new ProductDTO(1L, "LAP-001", "UltraBook Pro 14", "Slim performance laptop with fast SSD storage and long battery life.", 245000.00, 15, "ultrabook-pro-14.png"), 7, 5, 3);
        addProduct(new ProductDTO(2L, "PHN-002", "Nova X Smartphone", "Large display smartphone with high-capacity battery and advanced camera.", 118000.00, 22, "nova-x-smartphone.png"), 9, 8, 5);
        addProduct(new ProductDTO(3L, "HDP-003", "Pulse Wireless Headphones", "Noise-cancelling wireless headphones with deep bass and soft ear cushions.", 32000.00, 8, "pulse-wireless-headphones.png"), 3, 3, 2);
        addProduct(new ProductDTO(4L, "KBD-004", "Orion Mechanical Keyboard", "RGB mechanical keyboard with tactile switches and durable aluminum frame.", 18500.00, 5, "orion-mechanical-keyboard.png"), 2, 2, 1);
        addProduct(new ProductDTO(5L, "MON-005", "Vision 27 Monitor", "27-inch full HD monitor with thin bezels and smooth display performance.", 68000.00, 12, "vision-27-monitor.png"), 5, 4, 3);
        addProduct(new ProductDTO(6L, "WCH-006", "Smart Watch S5", "Fitness smartwatch with heart-rate tracking, notifications, and bright display.", 42500.00, 18, "smart-watch-s5.png"), 8, 6, 4);
        addProduct(new ProductDTO(7L, "MOU-007", "Swift Wireless Mouse", "Ergonomic wireless mouse with silent clicks and precision tracking.", 9500.00, 30, "swift-wireless-mouse.png"), 12, 10, 8);
        addProduct(new ProductDTO(8L, "SPK-008", "BassBox Bluetooth Speaker", "Portable Bluetooth speaker with powerful stereo sound and long playback time.", 27500.00, 9, "bassbox-bluetooth-speaker.png"), 4, 3, 2);

        seedMetricDefaults();
    }

    private static void seedMetricDefaults() {
        putMetric("Database Status", DatabaseStore.connectionStatus(), "status");
        for (String name : MONITORED_METRICS) {
            if ("Database Status".equals(name)) {
                continue;
            }
            if (name.startsWith("Average ") || name.startsWith("Last ")) {
                putMetric(name, "0", "ms");
            } else if (name.startsWith("Latest ")) {
                putMetric(name, "No events yet", "event");
            } else {
                putMetric(name, "0", "count");
            }
        }
    }

    private static void addProduct(ProductDTO product, int colomboStock, int kandyStock, int galleStock) {
        PRODUCTS.put(product.getId(), product);
        Map<String, WarehouseStockDTO> stockMap = new LinkedHashMap<>();
        stockMap.put("CMB", new WarehouseStockDTO(product.getId(), product.getName(), "CMB", "Colombo Fulfillment Hub", colomboStock, 0));
        stockMap.put("KDY", new WarehouseStockDTO(product.getId(), product.getName(), "KDY", "Kandy Distribution Center", kandyStock, 0));
        stockMap.put("GAL", new WarehouseStockDTO(product.getId(), product.getName(), "GAL", "Galle Express Warehouse", galleStock, 0));
        WAREHOUSE_INVENTORY.put(product.getId(), stockMap);
        product.setAvailableQuantity(colomboStock + kandyStock + galleStock);
    }

    private static boolean databaseReady() {
        return DatabaseStore.isAvailable();
    }

    public static boolean isDatabaseConnected() {
        return databaseReady();
    }

    public static String databaseStatus() {
        if (databaseReady()) {
            return DatabaseStore.connectionStatus();
        }
        return "Disconnected - using local memory fallback";
    }

    public static UserDTO findUser(String username) {
        if (username == null) return null;
        if (databaseReady()) return DatabaseStore.findUser(username);
        // Demo accounts must never silently bypass a configured database.
        if (Boolean.getBoolean("techmart.demoAuth")) return USERS.get(username);
        return null;
    }

    public static List<UserDTO> users() {
        if (databaseReady()) {
            List<UserDTO> list = DatabaseStore.users();
            if (!list.isEmpty()) {
                return list;
            }
        }
        return new ArrayList<>(USERS.values());
    }

    public static ProductDTO findProduct(Long id) {
        if (databaseReady()) {
            ProductDTO product = DatabaseStore.findProduct(id);
            if (product != null) {
                int warehouseStock = DatabaseStore.stock(id);
                if (warehouseStock >= 0) {
                    product.setAvailableQuantity(warehouseStock);
                }
                return product;
            }
        }
        ProductDTO product = PRODUCTS.get(id);
        if (product != null) {
            product.setAvailableQuantity(stock(id));
        }
        return product;
    }

    public static List<ProductDTO> products() {
        if (databaseReady()) {
            List<ProductDTO> list = DatabaseStore.products();
            if (!list.isEmpty()) {
                for (ProductDTO product : list) {
                    int warehouseStock = DatabaseStore.stock(product.getId());
                    if (warehouseStock >= 0) {
                        product.setAvailableQuantity(warehouseStock);
                    }
                }
                return list;
            }
        }
        List<ProductDTO> list = new ArrayList<>();
        for (ProductDTO product : PRODUCTS.values()) {
            product.setAvailableQuantity(stock(product.getId()));
            list.add(product);
        }
        list.sort(Comparator.comparing(ProductDTO::getId));
        return list;
    }

    public static int stock(Long productId) {
        if (databaseReady()) {
            int dbStock = DatabaseStore.stock(productId);
            if (dbStock >= 0) {
                return dbStock;
            }
        }
        int total = 0;
        Map<String, WarehouseStockDTO> stockMap = WAREHOUSE_INVENTORY.get(productId);
        if (stockMap != null) {
            for (WarehouseStockDTO dto : stockMap.values()) {
                total += dto.getAvailableQuantity();
            }
        }
        return total;
    }

    public static int reservedStock(Long productId) {
        if (databaseReady()) {
            int dbReserved = DatabaseStore.reservedStock(productId);
            if (dbReserved >= 0) {
                return dbReserved;
            }
        }
        int total = 0;
        Map<String, WarehouseStockDTO> stockMap = WAREHOUSE_INVENTORY.get(productId);
        if (stockMap != null) {
            for (WarehouseStockDTO dto : stockMap.values()) {
                total += dto.getReservedQuantity();
            }
        }
        return total;
    }

    public static synchronized boolean reserve(Long productId, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        if (databaseReady()) {
            return DatabaseStore.reserve(productId, quantity);
        }
        Map<String, WarehouseStockDTO> stockMap = WAREHOUSE_INVENTORY.get(productId);
        if (stockMap == null || stock(productId) < quantity) {
            return false;
        }
        int remaining = quantity;
        List<WarehouseStockDTO> rows = new ArrayList<>(stockMap.values());
        rows.sort(Comparator.comparing(WarehouseStockDTO::getAvailableQuantity).reversed());
        for (WarehouseStockDTO row : rows) {
            if (remaining == 0) {
                break;
            }
            int take = Math.min(row.getAvailableQuantity(), remaining);
            if (take > 0) {
                row.setAvailableQuantity(row.getAvailableQuantity() - take);
                row.setReservedQuantity(row.getReservedQuantity() + take);
                remaining -= take;
            }
        }
        ProductDTO product = PRODUCTS.get(productId);
        if (product != null) {
            product.setAvailableQuantity(stock(productId));
        }
        return remaining == 0;
    }

    public static synchronized void release(Long productId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        if (databaseReady()) {
            DatabaseStore.release(productId, quantity);
            return;
        }
        Map<String, WarehouseStockDTO> stockMap = WAREHOUSE_INVENTORY.get(productId);
        if (stockMap == null || stockMap.isEmpty()) {
            return;
        }
        int remaining = quantity;
        for (WarehouseStockDTO row : stockMap.values()) {
            if (remaining == 0) {
                break;
            }
            int release = Math.min(row.getReservedQuantity(), remaining);
            if (release > 0) {
                row.setReservedQuantity(row.getReservedQuantity() - release);
                row.setAvailableQuantity(row.getAvailableQuantity() + release);
                remaining -= release;
            }
        }
        if (remaining > 0) {
            WarehouseStockDTO first = stockMap.values().iterator().next();
            first.setAvailableQuantity(first.getAvailableQuantity() + remaining);
        }
        ProductDTO product = PRODUCTS.get(productId);
        if (product != null) {
            product.setAvailableQuantity(stock(productId));
        }
    }

    public static List<WarehouseStockDTO> warehouseStock() {
        if (databaseReady()) {
            List<WarehouseStockDTO> list = DatabaseStore.warehouseStock();
            if (!list.isEmpty()) {
                return list;
            }
        }
        List<WarehouseStockDTO> list = new ArrayList<>();
        for (Map<String, WarehouseStockDTO> stockMap : WAREHOUSE_INVENTORY.values()) {
            for (WarehouseStockDTO dto : stockMap.values()) {
                list.add(copyWarehouseStock(dto));
            }
        }
        list.sort(Comparator.comparing(WarehouseStockDTO::getProductId).thenComparing(WarehouseStockDTO::getWarehouseCode));
        return list;
    }

    public static List<WarehouseStockDTO> warehouseStockByProduct(Long productId) {
        if (databaseReady()) {
            List<WarehouseStockDTO> list = DatabaseStore.warehouseStockByProduct(productId);
            if (!list.isEmpty()) {
                return list;
            }
        }
        List<WarehouseStockDTO> list = new ArrayList<>();
        Map<String, WarehouseStockDTO> stockMap = WAREHOUSE_INVENTORY.get(productId);
        if (stockMap != null) {
            for (WarehouseStockDTO dto : stockMap.values()) {
                list.add(copyWarehouseStock(dto));
            }
        }
        return list;
    }

    public static void synchronizeInventory() {
        if (databaseReady()) {
            DatabaseStore.synchronizeProductStockFromWarehouses();
        }
        for (ProductDTO product : PRODUCTS.values()) {
            product.setAvailableQuantity(stock(product.getId()));
        }
        incrementMetric("Inventory Sync Runs");
    }

    private static WarehouseStockDTO copyWarehouseStock(WarehouseStockDTO dto) {
        return new WarehouseStockDTO(dto.getProductId(), dto.getProductName(), dto.getWarehouseCode(), dto.getWarehouseName(),
                dto.getAvailableQuantity(), dto.getReservedQuantity());
    }

    public static Long nextOrderId() {
        if (databaseReady()) {
            Long next = DatabaseStore.nextOrderId();
            if (next != null) {
                return next;
            }
        }
        return ORDER_ID.incrementAndGet();
    }

    public static void saveOrder(OrderDTO order) {
        if (databaseReady()) {
            if (DatabaseStore.saveOrder(order)) {
                return;
            }
            METRICS.put("Database Write Failures", new MetricDTO("Database Write Failures", "1", "count", LocalDateTime.now()));
        }
        ORDERS.add(order);
    }

    public static OrderDTO findOrder(Long orderId) {
        if (databaseReady()) {
            OrderDTO dbOrder = DatabaseStore.findOrder(orderId);
            if (dbOrder != null) {
                return dbOrder;
            }
        }
        for (OrderDTO order : ORDERS) {
            if (order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public static List<OrderDTO> orders() {
        if (databaseReady()) {
            List<OrderDTO> list = DatabaseStore.orders();
            if (!list.isEmpty()) {
                return list;
            }
        }
        List<OrderDTO> list = new ArrayList<>(ORDERS);
        list.sort(Comparator.comparing(OrderDTO::getId).reversed());
        return list;
    }

    public static void updateOrderStatus(Long orderId, String status) {
        if (databaseReady()) {
            DatabaseStore.updateOrderStatus(orderId, status);
        }
        for (OrderDTO order : ORDERS) {
            if (order.getId().equals(orderId)) {
                order.setStatus(status);
                return;
            }
        }
    }

    public static void saveNotification(Long orderId, String username, String channel, String message, String status) {
        if (databaseReady() && DatabaseStore.saveNotification(orderId, username, channel, message, status)) {
            return;
        }
        incrementMetric("Notifications Stored");
    }

    public static void putMetric(String name, String value, String unit) {
        if (!isMetricTracked(name)) {
            return;
        }
        if (databaseReady()) {
            DatabaseStore.putMetric(name, value, unit);
        }
        METRICS.put(name, new MetricDTO(name, value, unit, LocalDateTime.now()));
    }

    public static void incrementMetric(String name) {
        if (!isMetricTracked(name)) {
            return;
        }
        if (databaseReady()) {
            DatabaseStore.incrementMetric(name);
        }
        MetricDTO metric = METRICS.get(name);
        long current = 0;
        String unit = "count";
        if (metric != null) {
            try {
                current = Long.parseLong(metric.getValue());
            } catch (NumberFormatException ignored) {
                current = 0;
            }
            unit = metric.getUnit();
        }
        METRICS.put(name, new MetricDTO(name, String.valueOf(current + 1), unit, LocalDateTime.now()));
    }

    public static void recordTimedMetric(String name, long elapsedMillis) {
        TIMER_TOTALS.merge(name, elapsedMillis, Long::sum);
        TIMER_COUNTS.merge(name, 1L, Long::sum);
        long count = TIMER_COUNTS.getOrDefault(name, 1L);
        long total = TIMER_TOTALS.getOrDefault(name, elapsedMillis);
        putMetric("Last " + name, String.valueOf(elapsedMillis), "ms");
        putMetric("Average " + name, String.valueOf(total / count), "ms");
    }

    public static List<MetricDTO> metrics() {
        if (databaseReady()) {
            DatabaseStore.putMetric("Database Status", DatabaseStore.connectionStatus(), "status");
            List<MetricDTO> list = new ArrayList<>();
            for (MetricDTO metric : DatabaseStore.metrics()) {
                if (isMetricTracked(metric.getName())) {
                    list.add(metric);
                }
            }
            list.sort(Comparator.comparing(MetricDTO::getName));
            if (!list.isEmpty()) {
                return list;
            }
        }
        METRICS.put("Database Status", new MetricDTO("Database Status", databaseStatus(), "status", LocalDateTime.now()));
        List<MetricDTO> list = new ArrayList<>();
        for (MetricDTO metric : METRICS.values()) {
            if (isMetricTracked(metric.getName())) {
                list.add(metric);
            }
        }
        list.sort(Comparator.comparing(MetricDTO::getName));
        return list;
    }
}
