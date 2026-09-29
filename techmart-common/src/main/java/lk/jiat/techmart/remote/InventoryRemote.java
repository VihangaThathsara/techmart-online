package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.dto.WarehouseStockDTO;

import java.util.List;

@Remote
public interface InventoryRemote {
    int getAvailableQuantity(Long productId);
    boolean reserveStock(Long productId, int quantity);
    void releaseStock(Long productId, int quantity);
    List<ProductDTO> getLowStockProducts();
    List<WarehouseStockDTO> getWarehouseStock();
    List<WarehouseStockDTO> getWarehouseStockByProduct(Long productId);
    int getReservedQuantity(Long productId);
    void synchronizeInventory();
}
