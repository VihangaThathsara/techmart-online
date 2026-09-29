package lk.jiat.techmart.store;

import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechMartPerformanceTest {

    @Test
    void productCatalogueLookupShouldStayWithinPrototypeBenchmark() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            List<ProductDTO> products = TechMartStore.products();
            assertFalse(products.isEmpty());
        }
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        TechMartStore.recordTimedMetric("Product Catalogue Lookup Time", elapsedMillis);
        assertTrue(elapsedMillis < 2000, "1000 catalogue reads should complete within 2 seconds in local prototype mode");
    }

    @Test
    void warehouseReservationShouldKeepInventoryConsistentUnderRepeatedOperations() {
        int before = TechMartStore.stock(7L);
        long start = System.nanoTime();
        for (int i = 0; i < 25; i++) {
            assertTrue(TechMartStore.reserve(7L, 1));
        }
        for (int i = 0; i < 25; i++) {
            TechMartStore.release(7L, 1);
        }
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        TechMartStore.recordTimedMetric("Inventory Reservation Time", elapsedMillis);

        int after = TechMartStore.stock(7L);
        assertTrue(elapsedMillis < 2000, "Repeated reservation/release cycle should complete within 2 seconds");
        assertTrue(after >= before, "Released stock should not be lower than the original stock");
        List<WarehouseStockDTO> rows = TechMartStore.warehouseStockByProduct(7L);
        assertFalse(rows.isEmpty());
    }
}
