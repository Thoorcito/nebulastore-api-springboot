package cl.thoorcito.nebulastore.application.service;

public record OrderItemRequest(
    Long productId,
    int quantity,
    Double dimensionX,
    Double dimensionY,
    Double dimensionZ
) {
}