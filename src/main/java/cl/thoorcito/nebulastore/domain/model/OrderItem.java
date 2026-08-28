package cl.thoorcito.nebulastore.domain.model;

public record OrderItem(
    Long id,
    Long orderId,
    Long productId,
    String productCode,
    String productName,
    int quantity,
    double unitPrice,
    Double dimensionX,     // null si el producto no es CUSTOM_PRINT
    Double dimensionY,
    Double dimensionZ
) {
}