package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.OrderDTO;

import java.util.concurrent.Future;

@Remote
public interface NotificationRemote {
    Future<String> sendOrderNotificationAsync(OrderDTO order);
}
