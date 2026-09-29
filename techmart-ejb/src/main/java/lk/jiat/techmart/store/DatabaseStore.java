package lk.jiat.techmart.store;

import lk.jiat.techmart.dto.CartItemDTO;
import lk.jiat.techmart.dto.MetricDTO;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.UserDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;
import lk.jiat.techmart.config.EnterpriseResourceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.io.InputStream;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

final class DatabaseStore {
    private static final Logger LOGGER = Logger.getLogger(DatabaseStore.class.getName());
    private static final String DEFAULT_JDBC_URL =
            "jdbc:mysql://localhost:3306/techmart_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo";
    private static final String[] DATASOURCE_NAMES = {
            EnterpriseResourceConfig.JDBC_RESOURCE,
            "java:app/jdbc/TechMartDS",
            "java:module/jdbc/TechMartDS",
            "java:comp/env/jdbc/TechMartDS",
            "jdbc/TechMartDS"
    };
    private static volatile DataSource dataSource;
    private static volatile Properties dbProperties;
    private static volatile long unavailableUntilMillis = 0L;

    private DatabaseStore() {
    }

    static boolean isAvailable() {
        long now = System.currentTimeMillis();
        if (now < unavailableUntilMillis) {
            return false;
        }
        try (Connection ignored = connection()) {
            return true;
        } catch (Exception e) {
            unavailableUntilMillis = now + 5000L;
            LOGGER.log(Level.FINE, "Database unavailable, local memory fallback will be used: " + e.getMessage());
            return false;
        }
    }

    private static Connection connection() throws Exception {
        DataSource ds = resolveDataSource();
        Connection connection;
        if (ds != null) {
            connection = ds.getConnection();
        } else {
            connection = directConnection();
        }
        connection.setAutoCommit(true);
        return connection;
    }

    private static DataSource resolveDataSource() {
        DataSource cached = dataSource;
        if (cached != null) {
            return cached;
        }
        for (String name : DATASOURCE_NAMES) {
            try {
                Object lookedUp = new InitialContext().lookup(name);
                if (lookedUp instanceof DataSource) {
                    dataSource = (DataSource) lookedUp;
                    LOGGER.info("Using configured JDBC connection pool: " + name);
                    return dataSource;
                }
            } catch (NamingException ignored) {
                // Keep trying the next standard JNDI name, then fall back for unit tests/local demos.
            }
        }
        return null;
    }

    private static Connection directConnection() throws Exception {
        String directUrl = value("techmart.jdbc.url", "TECHMART_JDBC_URL", DEFAULT_JDBC_URL);
        String directUser = value("techmart.jdbc.user", "TECHMART_JDBC_USER", "root");
        String directPassword = value("techmart.jdbc.password", "TECHMART_JDBC_PASSWORD", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver", true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException ignored) {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }

        return DriverManager.getConnection(directUrl, directUser, directPassword);
    }

    private static String value(String propertyName, String envName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.trim().isEmpty()) {
            return property.trim();
        }

        String env = System.getenv(envName);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }

        String fileValue = databaseProperties().getProperty(propertyName);
        if (fileValue != null && !fileValue.trim().isEmpty()) {
            return fileValue.trim();
        }

        return defaultValue;
    }

    private static Properties databaseProperties() {
        Properties cached = dbProperties;
        if (cached != null) {
            return cached;
        }

        Properties loaded = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("techmart-db.properties")) {
            if (in != null) {
                loaded.load(in);
                LOGGER.info("Loaded database connection settings from techmart-db.properties");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load techmart-db.properties: " + e.getMessage());
        }
        dbProperties = loaded;
        return loaded;
    }

    private static void logFallback(String operation, Exception e) {
        LOGGER.log(Level.FINE, "DatabaseStore " + operation + " fallback -> " + e.getMessage());
    }

    static String connectionStatus() {
        long now = System.currentTimeMillis();
        if (now < unavailableUntilMillis) {
            return "Disconnected";
        }
        try (Connection con = connection()) {
            String catalog = con.getCatalog();
            String databaseName = (catalog == null || catalog.trim().isEmpty()) ? "techmart_db" : catalog.trim();
            return "Connected (" + databaseName + ")";
        } catch (Exception e) {
            unavailableUntilMillis = now + 5000L;
            return "Disconnected";
        }
    }

    static UserDTO findUser(String username) {
        String sql = "SELECT id, username, password, first_name, email, role FROM users WHERE username=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            logFallback("findUser", e);
        }
        return null;
    }

    static List<UserDTO> users() {
        List<UserDTO> list = new ArrayList<>();
        String sql = "SELECT id, username, password, first_name, email, role FROM users ORDER BY id";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (Exception e) {
            logFallback("users", e);
        }
        return list;
    }

    static ProductDTO findProduct(Long id) {
        String sql = "SELECT id, sku, name, description, price, available_quantity, image_url FROM products WHERE id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (Exception e) {
            logFallback("findProduct", e);
        }
        return null;
    }

    static List<ProductDTO> products() {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT id, sku, name, description, price, available_quantity, image_url FROM products ORDER BY id";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            logFallback("products", e);
        }
        return list;
    }

    static int stock(Long productId) {
        String warehouseSql = "SELECT COALESCE(SUM(available_quantity), -1) AS available_qty FROM warehouse_inventory WHERE product_id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(warehouseSql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_qty");
                }
            }
        } catch (Exception e) {
            logFallback("warehouse stock", e);
        }

        String productSql = "SELECT available_quantity FROM products WHERE id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(productSql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_quantity");
                }
            }
        } catch (Exception e) {
            logFallback("product stock", e);
        }
        return -1;
    }

    static int reservedStock(Long productId) {
        String sql = "SELECT COALESCE(SUM(reserved_quantity), -1) AS reserved_qty FROM warehouse_inventory WHERE product_id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reserved_qty");
                }
            }
        } catch (Exception e) {
            logFallback("reservedStock", e);
        }
        return -1;
    }

    static synchronized boolean reserve(Long productId, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        String selectSql = "SELECT warehouse_id, available_quantity, reserved_quantity FROM warehouse_inventory WHERE product_id=? ORDER BY available_quantity DESC FOR UPDATE";
        String updateWarehouseSql = "UPDATE warehouse_inventory SET available_quantity=?, reserved_quantity=? WHERE warehouse_id=? AND product_id=?";
        try (Connection con = connection()) {
            con.setAutoCommit(false);
            try (PreparedStatement select = con.prepareStatement(selectSql)) {
                select.setLong(1, productId);
                List<long[]> rows = new ArrayList<>();
                int available = 0;
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        long warehouseId = rs.getLong("warehouse_id");
                        int rowAvailable = rs.getInt("available_quantity");
                        int rowReserved = rs.getInt("reserved_quantity");
                        rows.add(new long[]{warehouseId, rowAvailable, rowReserved});
                        available += rowAvailable;
                    }
                }
                if (available < quantity) {
                    con.rollback();
                    return false;
                }
                int remaining = quantity;
                try (PreparedStatement update = con.prepareStatement(updateWarehouseSql)) {
                    for (long[] row : rows) {
                        if (remaining == 0) {
                            break;
                        }
                        int rowAvailable = (int) row[1];
                        int rowReserved = (int) row[2];
                        int take = Math.min(rowAvailable, remaining);
                        if (take > 0) {
                            update.setInt(1, rowAvailable - take);
                            update.setInt(2, rowReserved + take);
                            update.setLong(3, row[0]);
                            update.setLong(4, productId);
                            update.addBatch();
                            remaining -= take;
                        }
                    }
                    update.executeBatch();
                }
                synchronizeProductStockFromWarehouses(con, productId);
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            logFallback("warehouse reserve", e);
        }

        return reserveProductTableOnly(productId, quantity);
    }

    private static synchronized boolean reserveProductTableOnly(Long productId, int quantity) {
        String selectSql = "SELECT available_quantity FROM products WHERE id=? FOR UPDATE";
        String updateSql = "UPDATE products SET available_quantity=? WHERE id=?";
        try (Connection con = connection()) {
            con.setAutoCommit(false);
            try (PreparedStatement select = con.prepareStatement(selectSql)) {
                select.setLong(1, productId);
                try (ResultSet rs = select.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false;
                    }
                    int current = rs.getInt("available_quantity");
                    if (current < quantity) {
                        con.rollback();
                        return false;
                    }
                    try (PreparedStatement update = con.prepareStatement(updateSql)) {
                        update.setInt(1, current - quantity);
                        update.setLong(2, productId);
                        update.executeUpdate();
                    }
                    con.commit();
                    return true;
                }
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            logFallback("product reserve", e);
        }
        return false;
    }

    static synchronized void release(Long productId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        String selectSql = "SELECT warehouse_id, reserved_quantity FROM warehouse_inventory WHERE product_id=? ORDER BY warehouse_id FOR UPDATE";
        String updateSql = "UPDATE warehouse_inventory SET available_quantity = available_quantity + ?, reserved_quantity = GREATEST(reserved_quantity - ?, 0) WHERE warehouse_id=? AND product_id=?";
        try (Connection con = connection()) {
            con.setAutoCommit(false);
            try (PreparedStatement select = con.prepareStatement(selectSql);
                 PreparedStatement update = con.prepareStatement(updateSql)) {
                select.setLong(1, productId);
                int remaining = quantity;
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next() && remaining > 0) {
                        long warehouseId = rs.getLong("warehouse_id");
                        int reserved = rs.getInt("reserved_quantity");
                        int release = Math.min(reserved, remaining);
                        if (release > 0) {
                            update.setInt(1, release);
                            update.setInt(2, release);
                            update.setLong(3, warehouseId);
                            update.setLong(4, productId);
                            update.addBatch();
                            remaining -= release;
                        }
                    }
                }
                update.executeBatch();
                synchronizeProductStockFromWarehouses(con, productId);
                con.commit();
                return;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            logFallback("warehouse release", e);
        }

        String sql = "UPDATE products SET available_quantity = available_quantity + ? WHERE id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (Exception e) {
            logFallback("product release", e);
        }
    }

    static List<WarehouseStockDTO> warehouseStock() {
        List<WarehouseStockDTO> list = new ArrayList<>();
        String sql = "SELECT p.id AS product_id, p.name AS product_name, w.code AS warehouse_code, w.name AS warehouse_name, " +
                "wi.available_quantity, wi.reserved_quantity " +
                "FROM warehouse_inventory wi " +
                "JOIN warehouses w ON wi.warehouse_id = w.id " +
                "JOIN products p ON wi.product_id = p.id " +
                "ORDER BY p.id, w.code";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapWarehouseStock(rs));
            }
        } catch (Exception e) {
            logFallback("warehouseStock", e);
        }
        return list;
    }

    static List<WarehouseStockDTO> warehouseStockByProduct(Long productId) {
        List<WarehouseStockDTO> list = new ArrayList<>();
        String sql = "SELECT p.id AS product_id, p.name AS product_name, w.code AS warehouse_code, w.name AS warehouse_name, " +
                "wi.available_quantity, wi.reserved_quantity " +
                "FROM warehouse_inventory wi " +
                "JOIN warehouses w ON wi.warehouse_id = w.id " +
                "JOIN products p ON wi.product_id = p.id " +
                "WHERE p.id=? ORDER BY w.code";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWarehouseStock(rs));
                }
            }
        } catch (Exception e) {
            logFallback("warehouseStockByProduct", e);
        }
        return list;
    }

    static void synchronizeProductStockFromWarehouses() {
        try (Connection con = connection()) {
            synchronizeProductStockFromWarehouses(con, null);
        } catch (Exception e) {
            logFallback("synchronizeProductStockFromWarehouses", e);
        }
    }

    private static void synchronizeProductStockFromWarehouses(Connection con, Long productId) throws SQLException {
        String sql = "UPDATE products p SET available_quantity = " +
                "(SELECT COALESCE(SUM(wi.available_quantity), 0) FROM warehouse_inventory wi WHERE wi.product_id = p.id)";
        if (productId != null) {
            sql += " WHERE p.id=" + productId;
        }
        try (Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    static Long nextOrderId() {
        String sql = "SELECT COALESCE(MAX(id), 1000) + 1 AS next_id FROM orders";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("next_id");
            }
        } catch (Exception e) {
            logFallback("nextOrderId", e);
        }
        return null;
    }

    static boolean saveOrder(OrderDTO order) {
        String orderSql = "INSERT INTO orders (id, username, total_amount, status, created_at) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, line_total) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = connection()) {
            con.setAutoCommit(false);
            try (PreparedStatement orderPs = con.prepareStatement(orderSql);
                 PreparedStatement itemPs = con.prepareStatement(itemSql)) {
                orderPs.setLong(1, order.getId());
                orderPs.setString(2, order.getUsername());
                orderPs.setDouble(3, order.getTotalAmount());
                orderPs.setString(4, order.getStatus());
                orderPs.setTimestamp(5, Timestamp.valueOf(order.getCreatedAt()));
                orderPs.executeUpdate();

                for (CartItemDTO item : order.getItems()) {
                    itemPs.setLong(1, order.getId());
                    itemPs.setLong(2, item.getProductId());
                    itemPs.setString(3, item.getProductName());
                    itemPs.setDouble(4, item.getUnitPrice());
                    itemPs.setInt(5, item.getQuantity());
                    itemPs.setDouble(6, item.getLineTotal());
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            logFallback("saveOrder", e);
        }
        return false;
    }

    static OrderDTO findOrder(Long orderId) {
        String sql = "SELECT id, username, total_amount, status, created_at FROM orders WHERE id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderDTO order = mapOrder(rs);
                    order.setItems(orderItems(con, order.getId()));
                    return order;
                }
            }
        } catch (Exception e) {
            logFallback("findOrder", e);
        }
        return null;
    }

    static List<OrderDTO> orders() {
        List<OrderDTO> list = new ArrayList<>();
        String sql = "SELECT id, username, total_amount, status, created_at FROM orders ORDER BY id DESC";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderDTO order = mapOrder(rs);
                order.setItems(orderItems(con, order.getId()));
                list.add(order);
            }
        } catch (Exception e) {
            logFallback("orders", e);
        }
        return list;
    }

    static void updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            logFallback("updateOrderStatus", e);
        }
    }

    static boolean saveNotification(Long orderId, String username, String channel, String message, String status) {
        String sql = "INSERT INTO notifications (order_id, username, channel, message, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setString(2, username);
            ps.setString(3, channel);
            ps.setString(4, message);
            ps.setString(5, status);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            logFallback("saveNotification", e);
        }
        return false;
    }

    static void putMetric(String name, String value, String unit) {
        String sql = "INSERT INTO performance_logs (metric_name, metric_value, metric_unit, updated_at) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE metric_value=VALUES(metric_value), metric_unit=VALUES(metric_unit), updated_at=VALUES(updated_at)";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, value);
            ps.setString(3, unit);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (Exception e) {
            logFallback("putMetric", e);
        }
    }

    static void incrementMetric(String name) {
        MetricDTO existing = findMetric(name);
        long current = 0;
        String unit = "count";
        if (existing != null) {
            try {
                current = Long.parseLong(existing.getValue());
            } catch (NumberFormatException ignored) {
                current = 0;
            }
            unit = existing.getUnit();
        }
        putMetric(name, String.valueOf(current + 1), unit);
    }

    static List<MetricDTO> metrics() {
        List<MetricDTO> list = new ArrayList<>();
        String sql = "SELECT metric_name, metric_value, metric_unit, updated_at FROM performance_logs ORDER BY metric_name";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MetricDTO(
                        rs.getString("metric_name"),
                        rs.getString("metric_value"),
                        rs.getString("metric_unit"),
                        toLocalDateTime(rs.getTimestamp("updated_at"))
                ));
            }
        } catch (Exception e) {
            logFallback("metrics", e);
        }
        return list;
    }

    private static MetricDTO findMetric(String name) {
        String sql = "SELECT metric_name, metric_value, metric_unit, updated_at FROM performance_logs WHERE metric_name=?";
        try (Connection con = connection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MetricDTO(
                            rs.getString("metric_name"),
                            rs.getString("metric_value"),
                            rs.getString("metric_unit"),
                            toLocalDateTime(rs.getTimestamp("updated_at"))
                    );
                }
            }
        } catch (Exception e) {
            logFallback("findMetric", e);
        }
        return null;
    }

    private static List<CartItemDTO> orderItems(Connection con, Long orderId) throws SQLException {
        List<CartItemDTO> items = new ArrayList<>();
        String sql = "SELECT product_id, product_name, unit_price, quantity FROM order_items WHERE order_id=? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new CartItemDTO(
                            rs.getLong("product_id"),
                            rs.getString("product_name"),
                            rs.getDouble("unit_price"),
                            rs.getInt("quantity")
                    ));
                }
            }
        }
        return items;
    }

    private static UserDTO mapUser(ResultSet rs) throws SQLException {
        return new UserDTO(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("email"),
                rs.getString("role")
        );
    }

    private static ProductDTO mapProduct(ResultSet rs) throws SQLException {
        return new ProductDTO(
                rs.getLong("id"),
                rs.getString("sku"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("available_quantity"),
                rs.getString("image_url")
        );
    }

    private static OrderDTO mapOrder(ResultSet rs) throws SQLException {
        return new OrderDTO(
                rs.getLong("id"),
                rs.getString("username"),
                new ArrayList<>(),
                rs.getDouble("total_amount"),
                rs.getString("status"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
    }

    private static WarehouseStockDTO mapWarehouseStock(ResultSet rs) throws SQLException {
        return new WarehouseStockDTO(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getString("warehouse_code"),
                rs.getString("warehouse_name"),
                rs.getInt("available_quantity"),
                rs.getInt("reserved_quantity")
        );
    }

    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? LocalDateTime.now() : timestamp.toLocalDateTime();
    }
}
