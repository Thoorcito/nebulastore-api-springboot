package cl.thoorcito.nebulastore.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lado "N" de la relacion con Order: cada item pertenece a UN pedido.
    // @JoinColumn(name = "order_id"): asi se llama la columna FK en la tabla.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // Lado "N" de la relacion con Product: cada item referencia UN producto.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    // Nullable: solo se usan si el producto es CUSTOM_PRINT
    private Double dimensionX;
    private Double dimensionY;
    private Double dimensionZ;

    public OrderItemEntity() {}

    public OrderItemEntity(OrderEntity order, ProductEntity product, int quantity, double unitPrice,
        Double dimensionX, Double dimensionY, Double dimensionZ) {
            
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
    }

    public Long getId() { return id; }
    public OrderEntity getOrder() { return order; }
    public ProductEntity getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public Double getDimensionX() { return dimensionX; }
    public Double getDimensionY() { return dimensionY; }
    public Double getDimensionZ() { return dimensionZ; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public void setProduct(ProductEntity product) { this.product = product; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setDimensionX(Double dimensionX) { this.dimensionX = dimensionX; }
    public void setDimensionY(Double dimensionY) { this.dimensionY = dimensionY; }
    public void setDimensionZ(Double dimensionZ) { this.dimensionZ = dimensionZ; }
}