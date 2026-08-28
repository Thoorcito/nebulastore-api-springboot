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

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String status; // "PENDING" | "CONFIRMED" | "CANCELLED"

    @Column(nullable = false)
    private double total;

    // Un pedido tiene muchas lineas (1:N).
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity() {}

    public OrderEntity(String code, String customerName, String status, double total) {
        this.code = code;
        this.customerName = customerName;
        this.status = status;
        this.total = total;
    }

    public OrderEntity(Long id, String code, String customerName, String status, double total) {
        this.id = id;
        this.code = code;
        this.customerName = customerName;
        this.status = status;
        this.total = total;
    }

    // Metodo de conveniencia: agrega una linea y mantiene la relacion
    // bidireccional consistente (el item tambien apunta de vuelta a este pedido).
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public List<OrderItemEntity> getItems() { return items; }

    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStatus(String status) { this.status = status; }
    public void setTotal(double total) { this.total = total; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }
}