package cl.thoorcito.nebulastore.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.thoorcito.nebulastore.application.service.OrderItemRequest;
import cl.thoorcito.nebulastore.application.service.OrderService;
import cl.thoorcito.nebulastore.domain.model.Order;
import cl.thoorcito.nebulastore.domain.model.OrderItem;
import cl.thoorcito.nebulastore.infrastructure.web.dto.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders().stream().map(this::toResponseWithoutItems).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderService.getOrderItems(id);
        return ResponseEntity.ok(toResponse(order, items));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        // Mapea el DTO web (con validaciones) al record interno de aplicacion
        // (sin anotaciones de Spring) que espera el service.
        List<OrderItemRequest> itemRequests = request.items().stream()
                .map(i -> new OrderItemRequest(i.productId(), i.quantity(),
                        i.dimensionX(), i.dimensionY(), i.dimensionZ()))
                .toList();

        Order created = orderService.createOrder(request.customerName(), itemRequests);
        List<OrderItem> items = orderService.getOrderItems(created.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created, items));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItems(@PathVariable Long id) {
        List<OrderItemResponseDto> items = orderService.getOrderItems(id).stream()
                .map(this::toItemResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private OrderResponseDto toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponseDto> itemDtos = items.stream().map(this::toItemResponse).toList();
        return new OrderResponseDto(order.id(), order.code(), order.customerName(),
                order.status(), order.total(), itemDtos);
    }

    private OrderResponseDto toResponseWithoutItems(Order order) {
        return new OrderResponseDto(order.id(), order.code(), order.customerName(),
                order.status(), order.total(), List.of());
    }

    private OrderItemResponseDto toItemResponse(OrderItem item) {
        return new OrderItemResponseDto(item.id(), item.productId(), item.productCode(),
                item.productName(), item.quantity(), item.unitPrice(),
                item.dimensionX(), item.dimensionY(), item.dimensionZ());
    }
}