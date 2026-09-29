CREATE DATABASE IF NOT EXISTS techmart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE techmart_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS warehouse_inventory;
DROP TABLE IF EXISTS warehouses;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS performance_logs;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    available_quantity INT NOT NULL,
    image_url VARCHAR(180) NOT NULL,
    INDEX idx_products_sku (sku),
    INDEX idx_products_stock (available_quantity)
) ENGINE=InnoDB;

CREATE TABLE warehouses (
    id BIGINT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE warehouse_inventory (
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    available_quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (warehouse_id, product_id),
    INDEX idx_warehouse_inventory_product (product_id),
    INDEX idx_warehouse_inventory_available (available_quantity),
    CONSTRAINT fk_warehouse_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_warehouse_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_orders_username (username),
    INDEX idx_orders_status (status),
    INDEX idx_orders_created (created_at),
    CONSTRAINT fk_orders_user FOREIGN KEY (username) REFERENCES users(username)
) ENGINE=InnoDB;

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    INDEX idx_order_items_order (order_id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    message VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notifications_order (order_id),
    INDEX idx_notifications_user (username),
    CONSTRAINT fk_notifications_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE performance_logs (
    metric_name VARCHAR(140) PRIMARY KEY,
    metric_value VARCHAR(255) NOT NULL,
    metric_unit VARCHAR(40) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO users (id, username, password, first_name, email, role) VALUES
(1, 'admin', 'pbkdf2$600000$WhhM8y+ynhIDTdN5aiXc1Q==$BFwOuwLU3XHwcl3I9GWJiq/bfZf3UxaWtTcrHQSEqJQ=', 'TechMart Admin', 'admin@techmart.lk', 'ADMIN'),
(2, 'vihanga', 'pbkdf2$600000$WhhM8y+ynhIDTdN5aiXc1Q==$BFwOuwLU3XHwcl3I9GWJiq/bfZf3UxaWtTcrHQSEqJQ=', 'Vihanga', 'vihanga@techmart.lk', 'USER');


INSERT INTO products (id, sku, name, description, price, available_quantity, image_url) VALUES
(1, 'LAP-001', 'UltraBook Pro 14', 'Slim performance laptop with fast SSD storage and long battery life.', 245000.00, 15, 'ultrabook-pro-14.png'),
(2, 'PHN-002', 'Nova X Smartphone', 'Large display smartphone with high-capacity battery and advanced camera.', 118000.00, 22, 'nova-x-smartphone.png'),
(3, 'HDP-003', 'Pulse Wireless Headphones', 'Noise-cancelling wireless headphones with deep bass and soft ear cushions.', 32000.00, 8, 'pulse-wireless-headphones.png'),
(4, 'KBD-004', 'Orion Mechanical Keyboard', 'RGB mechanical keyboard with tactile switches and durable aluminum frame.', 18500.00, 5, 'orion-mechanical-keyboard.png'),
(5, 'MON-005', 'Vision 27 Monitor', '27-inch full HD monitor with thin bezels and smooth display performance.', 68000.00, 12, 'vision-27-monitor.png'),
(6, 'WCH-006', 'Smart Watch S5', 'Fitness smartwatch with heart-rate tracking, notifications, and bright display.', 42500.00, 18, 'smart-watch-s5.png'),
(7, 'MOU-007', 'Swift Wireless Mouse', 'Ergonomic wireless mouse with silent clicks and precision tracking.', 9500.00, 30, 'swift-wireless-mouse.png'),
(8, 'SPK-008', 'BassBox Bluetooth Speaker', 'Portable Bluetooth speaker with powerful stereo sound and long playback time.', 27500.00, 9, 'bassbox-bluetooth-speaker.png');

INSERT INTO warehouses (id, code, name, location, active) VALUES
(1, 'CMB', 'Colombo Fulfillment Hub', 'Colombo', TRUE),
(2, 'KDY', 'Kandy Distribution Center', 'Kandy', TRUE),
(3, 'GAL', 'Galle Express Warehouse', 'Galle', TRUE);

INSERT INTO warehouse_inventory (warehouse_id, product_id, available_quantity, reserved_quantity) VALUES
(1, 1, 7, 0), (2, 1, 5, 0), (3, 1, 3, 0),
(1, 2, 9, 0), (2, 2, 8, 0), (3, 2, 5, 0),
(1, 3, 3, 0), (2, 3, 3, 0), (3, 3, 2, 0),
(1, 4, 2, 0), (2, 4, 2, 0), (3, 4, 1, 0),
(1, 5, 5, 0), (2, 5, 4, 0), (3, 5, 3, 0),
(1, 6, 8, 0), (2, 6, 6, 0), (3, 6, 4, 0),
(1, 7, 12, 0), (2, 7, 10, 0), (3, 7, 8, 0),
(1, 8, 4, 0), (2, 8, 3, 0), (3, 8, 2, 0);

INSERT INTO performance_logs (metric_name, metric_value, metric_unit) VALUES
('Database Status', 'Ready', 'status'),
('Application Starts', '0', 'count'),
('Orders Received', '0', 'count'),
('Orders Completed', '0', 'count'),
('Orders Failed', '0', 'count'),
('JMS Messages Sent', '0', 'count'),
('JMS Send Failures', '0', 'count'),
('JMS Topic Events', '0', 'count'),
('JMS Topic Failures', '0', 'count'),
('MDB Messages Consumed', '0', 'count'),
('MDB Failures', '0', 'count'),
('Topic Events Consumed', '0', 'count'),
('Topic Event Failures', '0', 'count'),
('Admin Alerts Received', '0', 'count'),
('Admin Alert Failures', '0', 'count'),
('Async Tasks Started', '0', 'count'),
('Async Tasks Completed', '0', 'count'),
('Async Timeouts', '0', 'count'),
('Inventory Reservations', '0', 'count'),
('Inventory Reservation Failures', '0', 'count'),
('Inventory Releases', '0', 'count'),
('Inventory Sync Runs', '0', 'count'),
('Notifications Queued', '0', 'count'),
('Notifications Sent', '0', 'count'),
('Notifications Stored', '0', 'count'),
('Notification Failures', '0', 'count'),
('Database Write Failures', '0', 'count'),
('HTTP Requests', '0', 'count'),
('EJB Invocations', '0', 'count'),
('Average Order Processing Time', '0', 'ms'),
('Last Order Processing Time', '0', 'ms'),
('Average Order Placement Time', '0', 'ms'),
('Last Order Placement Time', '0', 'ms'),
('Average Product Catalogue Lookup Time', '0', 'ms'),
('Last Product Catalogue Lookup Time', '0', 'ms'),
('Average Inventory Reservation Time', '0', 'ms'),
('Last Inventory Reservation Time', '0', 'ms'),
('Last Order Time', '0', 'ms'),
('Last HTTP Time', '0', 'ms'),
('Last Order Failure', 'None', 'text'),
('Latest Store Event', 'No events yet', 'event'),
('Latest Stock Alert', 'No alerts yet', 'alert');

UPDATE products p SET available_quantity = (
    SELECT COALESCE(SUM(wi.available_quantity), 0)
    FROM warehouse_inventory wi
    WHERE wi.product_id = p.id
);
