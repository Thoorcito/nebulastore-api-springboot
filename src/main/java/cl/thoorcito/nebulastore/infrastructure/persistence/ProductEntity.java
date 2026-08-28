package cl.thoorcito.nebulastore.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// @Entity: le dice a Hibernate que esta clase mapea a una tabla.
// @Table(name = "products"): nombre explicito de la tabla en Postgres.
@Entity
@Table(name = "products")
public class ProductEntity {

    // @Id: clave primaria. @GeneratedValue(IDENTITY): Postgres genera el
    // valor automaticamente (columna SERIAL/BIGSERIAL), no lo asignamos nosotros.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // "FILAMENT" | "MACHINE" | "CUSTOM_PRINT"

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private int stockAvailable;

    // Un producto aparece en muchas lineas de pedido (1:N).
    // mappedBy = "product": el dueño de la relacion es OrderItemEntity
    // (tiene la columna product_id). cascade = ALL: si borro el producto,
    // Hibernate intenta propagar la operacion a sus OrderItems tambien.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // JPA exige un constructor vacio (lo usa Hibernate internamente via reflexion)
    public ProductEntity() {}

    public ProductEntity(String code, String name, String type, double unitPrice, int stockAvailable) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.unitPrice = unitPrice;
        this.stockAvailable = stockAvailable;
    }

    public ProductEntity(Long id, String code, String name, String type, double unitPrice, int stockAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.unitPrice = unitPrice;
        this.stockAvailable = stockAvailable;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getUnitPrice() { return unitPrice; }
    public int getStockAvailable() { return stockAvailable; }
    public List<OrderItemEntity> getOrderItems() { return orderItems; }

    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setStockAvailable(int stockAvailable) { this.stockAvailable = stockAvailable; }
}