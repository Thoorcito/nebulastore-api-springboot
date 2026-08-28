package cl.thoorcito.nebulastore.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
    @Schema(description = "ID del producto a comprar", example = "1")
    @NotNull(message = "El productId es obligatorio") Long productId,

    @Schema(description = "Cantidad solicitada", example = "2")
    @Min(value = 1, message = "La cantidad debe ser al menos 1") int quantity,

    @Schema(description = "Dimension X en mm (solo CUSTOM_PRINT)", example = "100")
    Double dimensionX,
    @Schema(description = "Dimension Y en mm (solo CUSTOM_PRINT)", example = "100")
    Double dimensionY,
    @Schema(description = "Dimension Z en mm (solo CUSTOM_PRINT)", example = "50")
    Double dimensionZ
) {
}