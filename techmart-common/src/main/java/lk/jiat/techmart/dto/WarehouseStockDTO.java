package lk.jiat.techmart.dto;

import java.io.Serializable;

public class WarehouseStockDTO implements Serializable {
    private Long productId;
    private String productName;
    private String warehouseCode;
    private String warehouseName;
    private int availableQuantity;
    private int reservedQuantity;

    public WarehouseStockDTO() {
    }

    public WarehouseStockDTO(Long productId, String productName, String warehouseCode, String warehouseName,
                             int availableQuantity, int reservedQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public int getTotalQuantity() { return availableQuantity + reservedQuantity; }
}
