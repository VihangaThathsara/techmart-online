package lk.jiat.techmart.dto;

import java.io.Serializable;

public class ProductDTO implements Serializable {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private double price;
    private int availableQuantity;
    private String imageUrl;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String sku, String name, String description, double price, int availableQuantity) {
        this(id, sku, name, description, price, availableQuantity, "product-default.png");
    }

    public ProductDTO(Long id, String sku, String name, String description, double price, int availableQuantity, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getImageUrl() {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return "product-default.png";
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", availableQuantity=" + availableQuantity +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
