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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Creacion y consulta de pedidos, con validacion de stock y dimensiones de impresion")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Listar todos los pedidos")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente") })
    @GetMapping
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders().stream().map(this::toResponseWithoutItems).toList();
    }

    @Operation(summary = "Obtener pedido por ID", description = "Devuelve el pedido junto con sus items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderService.getOrderItems(id);
        return ResponseEntity.ok(toResponse(order, items));
    }

    @Operation(summary = "Crear pedido", description = "Crea un pedido con uno o mas items, validando stock y dimensiones de impresion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos, cantidad invalida o dimensiones excedidas"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "422", description = "Stock insuficiente")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        List<OrderItemRequest> itemRequests = request.items().stream()
                .map(i -> new OrderItemRequest(i.productId(), i.quantity(),
                        i.dimensionX(), i.dimensionY(), i.dimensionZ()))
                .toList();

        Order created = orderService.createOrder(request.customerName(), itemRequests);
        List<OrderItem> items = orderService.getOrderItems(created.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created, items));
    }

    @Operation(summary = "Listar items de un pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
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