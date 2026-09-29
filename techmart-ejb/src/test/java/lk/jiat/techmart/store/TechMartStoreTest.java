package lk.jiat.techmart.store;

import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;
import lk.jiat.techmart.util.AppConstants;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechMartStoreTest {
    @Test
    void seededProductsAndUsersShouldExist() {
        assertFalse(TechMartStore.products().isEmpty());
        System.setProperty("techmart.demoAuth", "true");
        assertNotNull(TechMartStore.findUser("admin"));
        assertNotNull(TechMartStore.findUser("vihanga"));
        System.clearProperty("techmart.demoAuth");
    }

    @Test
    void multiWarehouseStockShouldBeAvailableForProducts() {
        List<WarehouseStockDTO> stockRows = TechMartStore.warehouseStockByProduct(1L);
        assertEquals(3, stockRows.size());
        int total = 0;
        for (WarehouseStockDTO row : stockRows) {
            total += row.getAvailableQuantity();
        }
        assertEquals(TechMartStore.stock(1L), total);
    }

    @Test
    void reservationShouldReduceAvailableStockAndReleaseShouldRestoreIt() {
        ProductDTO product = TechMartStore.findProduct(7L);
        assertNotNull(product);
        int before = TechMartStore.stock(7L);

        assertTrue(TechMartStore.reserve(7L, 2));
        assertEquals(before - 2, TechMartStore.stock(7L));
        assertTrue(TechMartStore.reservedStock(7L) >= 2);

        TechMartStore.release(7L, 2);
        assertEquals(before, TechMartStore.stock(7L));
    }

    @Test
    void orderStatusShouldBeUpdateable() {
        OrderDTO order = new OrderDTO(9090L, "vihanga", new ArrayList<>(), 1000.00,
                AppConstants.ORDER_STATUS_RECEIVED, LocalDateTime.now());
        TechMartStore.saveOrder(order);
        TechMartStore.updateOrderStatus(9090L, AppConstants.ORDER_STATUS_COMPLETED);
        OrderDTO saved = TechMartStore.findOrder(9090L);
        assertNotNull(saved);
        assertEquals(AppConstants.ORDER_STATUS_COMPLETED, saved.getStatus());
    }

    @Test
    void timedMetricsShouldCreateAverageAndSampleCount() {
        TechMartStore.recordTimedMetric("Order Processing Time", 20);
        TechMartStore.recordTimedMetric("Order Processing Time", 40);
        assertFalse(TechMartStore.metrics().isEmpty());
        assertNotNull(TechMartStore.metrics().stream()
                .filter(metric -> metric.getName().equals("Average Order Processing Time"))
                .findFirst()
                .orElse(null));
        assertNotNull(TechMartStore.metrics().stream()
                .filter(metric -> metric.getName().equals("Last Order Processing Time"))
                .findFirst()
                .orElse(null));
    }
}
