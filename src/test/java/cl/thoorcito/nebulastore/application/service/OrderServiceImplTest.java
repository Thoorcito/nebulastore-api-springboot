package cl.thoorcito.nebulastore.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.thoorcito.nebulastore.domain.exception.ExceedsBuildVolumeException;
import cl.thoorcito.nebulastore.domain.exception.OutOfStockException;
import cl.thoorcito.nebulastore.domain.exception.ResourceNotFoundException;
import cl.thoorcito.nebulastore.domain.model.Order;
import cl.thoorcito.nebulastore.infrastructure.persistence.OrderEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.ProductEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.OrderItemJpaRepository;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.OrderJpaRepository;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.ProductJpaRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderJpaRepository orderJpaRepository;
    @Mock private ProductJpaRepository productJpaRepository;
    @Mock private OrderItemJpaRepository orderItemJpaRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_success_decreasesStockAndCalculatesTotal() {
        // Arrange
        ProductEntity product = new ProductEntity(1L, "FIL-001", "PLA", "FILAMENT", 10000, 10);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        // simula que la BD asigna un id al guardar el pedido
        when(orderJpaRepository.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            return new OrderEntity(1L, o.getCode(), o.getCustomerName(), o.getStatus(), o.getTotal());
        });
        OrderItemRequest request = new OrderItemRequest(1L, 3, null, null, null);

        // Act
        Order result = orderService.createOrder("Felipe", List.of(request));

        // Assert
        assertEquals("CONFIRMED", result.status());
        assertEquals(30000.0, result.total());
        assertEquals(7, product.getStockAvailable()); // 10 - 3
    }

    @Test
    void createOrder_insufficientStock_throwsOutOfStockException() {
        // Arrange
        ProductEntity product = new ProductEntity(1L, "FIL-001", "PLA", "FILAMENT", 10000, 2);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        OrderItemRequest request = new OrderItemRequest(1L, 5, null, null, null);

        // Act & Assert
        assertThrows(OutOfStockException.class,
                () -> orderService.createOrder("Felipe", List.of(request)));
    }

    @Test
    void createOrder_productNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(productJpaRepository.findById(99L)).thenReturn(Optional.empty());
        OrderItemRequest request = new OrderItemRequest(99L, 1, null, null, null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("Felipe", List.of(request)));
    }

    @Test
    void createOrder_emptyItems_throwsIllegalArgumentException() {
        // Arrange: no hace falta preparar mocks, el pedido llega sin items directamente

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Felipe", List.of()));
    }

    @Test
    void createOrder_customPrintWithoutDimensions_throwsIllegalArgumentException() {
        // Arrange
        ProductEntity product = new ProductEntity(1L, "CP-001", "Pieza a medida", "CUSTOM_PRINT", 5000, 10);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        OrderItemRequest request = new OrderItemRequest(1L, 1, null, null, null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Felipe", List.of(request)));
    }

    @Test
    void createOrder_customPrintExceedsVolume_throwsExceedsBuildVolumeException() {
        // Arrange
        ProductEntity product = new ProductEntity(1L, "CP-001", "Pieza a medida", "CUSTOM_PRINT", 5000, 10);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        OrderItemRequest request = new OrderItemRequest(1L, 1, 300.0, 100.0, 50.0); // X excede 220mm

        // Act & Assert
        assertThrows(ExceedsBuildVolumeException.class,
                () -> orderService.createOrder("Felipe", List.of(request)));
    }

    @Test
    void getOrderById_notFound_throwsException() {
        // Arrange
        when(orderJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrderItems_orderNotFound_throwsException() {
        // Arrange
        when(orderJpaRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderItems(1L));
    }
}