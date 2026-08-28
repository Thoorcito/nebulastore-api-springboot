package cl.thoorcito.nebulastore.application.service;

import java.util.List;

import cl.thoorcito.nebulastore.domain.model.Order;
import cl.thoorcito.nebulastore.domain.model.OrderItem;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(String customerName, List<OrderItemRequest> items);
    List<OrderItem> getOrderItems(Long orderId);
}