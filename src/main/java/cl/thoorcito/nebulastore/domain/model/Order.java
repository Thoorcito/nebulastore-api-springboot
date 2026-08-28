package cl.thoorcito.nebulastore.domain.model;

public record Order(
    Long id,
    String code,
    String customerName,
    String status,         // "PENDING" | "CONFIRMED" | "CANCELLED"
    double total
) {
}