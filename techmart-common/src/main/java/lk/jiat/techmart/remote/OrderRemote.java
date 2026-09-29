package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.CartItemDTO;
import lk.jiat.techmart.dto.OrderDTO;

import java.util.List;
import java.util.concurrent.Future;

@Remote
public interface OrderRemote {
    OrderDTO placeOrder(String username, List<CartItemDTO> cartItems);
    Future<String> processOrderAsync(Long orderId);
    List<OrderDTO> getOrdersByUser(String username);
    List<OrderDTO> getAllOrders();
}
