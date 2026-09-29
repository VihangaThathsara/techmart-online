package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.ProductDTO;

import java.util.List;

@Remote
public interface ProductRemote {
    ProductDTO getProduct(Long id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> searchProducts(String keyword);
}
