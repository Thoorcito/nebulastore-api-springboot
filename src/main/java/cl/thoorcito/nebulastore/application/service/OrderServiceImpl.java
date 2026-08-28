package cl.thoorcito.nebulastore.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.thoorcito.nebulastore.domain.exception.ExceedsBuildVolumeException;
import cl.thoorcito.nebulastore.domain.exception.InvalidQuantityException;
import cl.thoorcito.nebulastore.domain.exception.OutOfStockException;
import cl.thoorcito.nebulastore.domain.exception.ResourceNotFoundException;
import cl.thoorcito.nebulastore.domain.model.Order;
import cl.thoorcito.nebulastore.domain.model.OrderItem;
import cl.thoorcito.nebulastore.infrastructure.persistence.OrderEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.OrderItemEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.ProductEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.OrderItemJpaRepository;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.OrderJpaRepository;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.ProductJpaRepository;

@Service
public class OrderServiceImpl implements OrderService {

    // Limites fisicos de la impresora (regla de negocio de NebulaStore,
    // equivalente a lo que antes validaba PrintDimensions como value object).
    private static final double MAX_X = 220.0;
    private static final double MAX_Y = 220.0;
    private static final double MAX_Z = 250.0;

    private final OrderJpaRepository orderJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    public OrderServiceImpl(OrderJpaRepository orderJpaRepository,ProductJpaRepository productJpaRepository,
        OrderItemJpaRepository orderItemJpaRepository) {
            
        this.orderJpaRepository = orderJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.orderItemJpaRepository = orderItemJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        OrderEntity entity = orderJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id '" + id + "' not found"));
        return toDomain(entity);
    }

    @Override
    @Transactional
    public Order createOrder(String customerName, List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un item");
        }

        String orderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        OrderEntity order = new OrderEntity(orderCode, customerName, "PENDING", 0.0);

        double total = 0.0;

        // Por cada linea solicitada: buscamos el producto, validamos las
        // reglas de negocio, descontamos stock, y armamos el item.
        for (OrderItemRequest req : items) {
            ProductEntity product = productJpaRepository.findById(req.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product with id '" + req.productId() + "' not found"));

            if (req.quantity() <= 0) {
                throw new InvalidQuantityException(
                        "La cantidad debe ser positiva, recibido: " + req.quantity());
            }

            if (product.getStockAvailable() < req.quantity()) {
                throw new OutOfStockException(
                        "Stock insuficiente para " + product.getCode() +
                        ": disponible " + product.getStockAvailable() +
                        ", solicitado " + req.quantity());
            }

            boolean isCustomPrint = "CUSTOM_PRINT".equals(product.getType());
            boolean hasDimensions = req.dimensionX() != null || req.dimensionY() != null || req.dimensionZ() != null;

            if (isCustomPrint) {
                if (req.dimensionX() == null || req.dimensionY() == null || req.dimensionZ() == null) {
                    throw new IllegalArgumentException(
                            "Los productos CUSTOM_PRINT requieren dimensionX, dimensionY y dimensionZ");
                }
                if (req.dimensionX() > MAX_X || req.dimensionY() > MAX_Y || req.dimensionZ() > MAX_Z) {
                    throw new ExceedsBuildVolumeException(
                            "La pieza excede el volumen de impresion (" + MAX_X + "x" + MAX_Y + "x" + MAX_Z +
                            " mm). Recibido: " + req.dimensionX() + "x" + req.dimensionY() + "x" + req.dimensionZ());
                }
            } else if (hasDimensions) {
                throw new IllegalArgumentException(
                        "Solo los productos CUSTOM_PRINT admiten dimensiones de impresion");
            }

            // Descuenta el stock del producto y persiste ese cambio.
            product.setStockAvailable(product.getStockAvailable() - req.quantity());
            productJpaRepository.save(product);

            OrderItemEntity itemEntity = new OrderItemEntity(
                    order, product, req.quantity(), product.getUnitPrice(),
                    req.dimensionX(), req.dimensionY(), req.dimensionZ());
            order.addItem(itemEntity); // mantiene la relacion bidireccional consistente
            total += product.getUnitPrice() * req.quantity();
        }

        order.setTotal(total);
        order.setStatus("CONFIRMED"); // ya se validaron y descontaron todos los items

        // cascade = ALL en OrderEntity.items: al guardar el pedido, Hibernate
        // guarda automaticamente todos los OrderItemEntity asociados.
        OrderEntity saved = orderJpaRepository.save(order);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        if (!orderJpaRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order with id '" + orderId + "' not found");
        }
        return orderItemJpaRepository.findByOrderId(orderId).stream()
                .map(this::toDomainItem)
                .toList();
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(entity.getId(), entity.getCode(), entity.getCustomerName(),
                entity.getStatus(), entity.getTotal());
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        return new OrderItem(
                entity.getId(), entity.getOrder().getId(), entity.getProduct().getId(),
                entity.getProduct().getCode(), entity.getProduct().getName(),
                entity.getQuantity(), entity.getUnitPrice(),
                entity.getDimensionX(), entity.getDimensionY(), entity.getDimensionZ());
    }
}