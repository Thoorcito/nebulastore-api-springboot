package cl.thoorcito.nebulastore.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
    @NotNull(message = "El productId es obligatorio") Long productId,
    @Min(value = 1, message = "La cantidad debe ser al menos 1") int quantity,
    Double dimensionX,   // opcional, solo si el producto es CUSTOM_PRINT
    Double dimensionY,
    Double dimensionZ
) {
}