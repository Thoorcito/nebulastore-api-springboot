package cl.thoorcito.nebulastore.infrastructure.web.dto;

public record OrderItemResponseDto(
    Long id,
    Long productId,
    String productCode,
    String productName,
    int quantity,
    double unitPrice,
    Double dimensionX,
    Double dimensionY,
    Double dimensionZ
) {
}