package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.CartItemDTO;
import lk.jiat.techmart.dto.ProductDTO;

import java.util.List;

@Remote
public interface CartRemote {
    void addItem(ProductDTO product, int quantity);
    void updateQuantity(Long productId, int quantity);
    void removeItem(Long productId);
    List<CartItemDTO> getItems();
    double getTotal();
    int getItemCount();
    void clear();
}
