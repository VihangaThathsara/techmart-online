package lk.jiat.techmart.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.producer.JMSProducerBean;
import lk.jiat.techmart.remote.InventoryRemote;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.store.TechMartStore;
import lk.jiat.techmart.util.AppConstants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class InventorySessionBean implements InventoryRemote {
    private static final Logger LOGGER = Logger.getLogger(InventorySessionBean.class.getName());


    @EJB
    private MetricsRemote metricsRemote;

    @EJB
    private JMSProducerBean jmsProducerBean;

    @Override
    public int getAvailableQuantity(Long productId) {
        return TechMartStore.stock(productId);
    }

    @Override
    public boolean reserveStock(Long productId, int quantity) {
        boolean reserved = TechMartStore.reserve(productId, quantity);
        if (reserved) {
            metricsRemote.incrementCounter("Inventory Reservations");
            ProductDTO product = TechMartStore.findProduct(productId);
            if (product != null && product.getAvailableQuantity() <= 5) {
                jmsProducerBean.publishInventoryEvent(AppConstants.EVENT_LOW_STOCK, productId,
                        "Low stock alert for " + product.getName() + " - available " + product.getAvailableQuantity());
            }
        } else {
            metricsRemote.incrementCounter("Inventory Reservation Failures");
        }
        LOGGER.info("Inventory reserve product=" + productId + " qty=" + quantity + " result=" + reserved);
        return reserved;
    }

    @Override
    public void releaseStock(Long productId, int quantity) {
        TechMartStore.release(productId, quantity);
        metricsRemote.incrementCounter("Inventory Releases");
        LOGGER.info("Inventory release product=" + productId + " qty=" + quantity);
    }

    @Override
    public List<ProductDTO> getLowStockProducts() {
        return TechMartStore.products().stream()
                .filter(product -> product.getAvailableQuantity() <= 8)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseStockDTO> getWarehouseStock() {
        return TechMartStore.warehouseStock();
    }

    @Override
    public List<WarehouseStockDTO> getWarehouseStockByProduct(Long productId) {
        return TechMartStore.warehouseStockByProduct(productId);
    }

    @Override
    public int getReservedQuantity(Long productId) {
        return TechMartStore.reservedStock(productId);
    }

    @Override
    public void synchronizeInventory() {
        TechMartStore.synchronizeInventory();
        jmsProducerBean.publishInventoryEvent(AppConstants.EVENT_INVENTORY_SYNCED, 0L, "Warehouse stock synchronized");
    }
}
