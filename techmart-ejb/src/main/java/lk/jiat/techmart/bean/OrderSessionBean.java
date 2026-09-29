package lk.jiat.techmart.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.dto.CartItemDTO;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.producer.JMSProducerBean;
import lk.jiat.techmart.remote.InventoryRemote;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.remote.NotificationRemote;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.store.TechMartStore;
import lk.jiat.techmart.util.AppConstants;
import lk.jiat.techmart.util.TimerUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class OrderSessionBean implements OrderRemote {
    private static final Logger LOGGER = Logger.getLogger(OrderSessionBean.class.getName());


    @EJB
    private InventoryRemote inventoryRemote;

    @EJB
    private MetricsRemote metricsRemote;

    @EJB
    private NotificationRemote notificationRemote;

    @EJB
    private JMSProducerBean jmsProducerBean;

    @EJB
    private AsyncOrderProcessorBean asyncOrderProcessorBean;

    @Override
    public OrderDTO placeOrder(String username, List<CartItemDTO> cartItems) {
        long start = TimerUtil.start();
        if (username == null || cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        List<CartItemDTO> reserved = new ArrayList<>();
        for (CartItemDTO item : cartItems) {
            boolean ok = inventoryRemote.reserveStock(item.getProductId(), item.getQuantity());
            if (!ok) {
                for (CartItemDTO reservedItem : reserved) {
                    inventoryRemote.releaseStock(reservedItem.getProductId(), reservedItem.getQuantity());
                }
                metricsRemote.incrementCounter("Orders Failed");
                throw new IllegalStateException("Not enough stock for product: " + item.getProductName());
            }
            reserved.add(item);
        }

        double total = 0;
        for (CartItemDTO item : cartItems) {
            total += item.getLineTotal();
        }

        OrderDTO order = new OrderDTO(
                TechMartStore.nextOrderId(),
                username,
                new ArrayList<>(cartItems),
                total,
                AppConstants.ORDER_STATUS_RECEIVED,
                LocalDateTime.now()
        );
        TechMartStore.saveOrder(order);

        long elapsed = TimerUtil.elapsedMillis(start);
        metricsRemote.incrementCounter("Orders Received");
        metricsRemote.recordMetric("Last Order Time", String.valueOf(elapsed), "ms");
        TechMartStore.recordTimedMetric("Order Placement Time", elapsed);

        jmsProducerBean.sendOrderMessage(order);
        jmsProducerBean.sendNotificationMessage(order);
        jmsProducerBean.publishOrderEvent(AppConstants.EVENT_ORDER_RECEIVED, order.getId(),
                "Order #" + order.getId() + " received from " + username);

        Future<String> notificationFuture = notificationRemote.sendOrderNotificationAsync(order);
        try {
            notificationFuture.get(150, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            metricsRemote.incrementCounter("Async Timeouts");
        } catch (Exception e) {
            metricsRemote.incrementCounter("Notification Failures");
        }

        LOGGER.info("Order placed " + order.getId() + " time=" + elapsed + "ms");
        return order;
    }

    @Override
    public Future<String> processOrderAsync(Long orderId) {
        return asyncOrderProcessorBean.processOrderAsync(orderId);
    }

    @Override
    public List<OrderDTO> getOrdersByUser(String username) {
        return TechMartStore.orders().stream()
                .filter(order -> order.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return TechMartStore.orders();
    }
}
