package lk.jiat.techmart.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.dto.CartItemDTO;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.CartRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateful
@Interceptors(PerformanceInterceptor.class)
public class CartSessionBean implements CartRemote {
    private static final Logger LOGGER = Logger.getLogger(CartSessionBean.class.getName());


    private final List<CartItemDTO> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        LOGGER.info("CartSessionBean init " + this);
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("CartSessionBean destroy " + this);
    }

    @Override
    public void addItem(ProductDTO product, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }
        for (CartItemDTO item : items) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItemDTO(product.getId(), product.getName(), product.getPrice(), quantity));
    }

    @Override
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }
        for (CartItemDTO item : items) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    @Override
    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    @Override
    public List<CartItemDTO> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (CartItemDTO item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (CartItemDTO item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    @Override
    @Remove
    public void clear() {
        items.clear();
    }
}
