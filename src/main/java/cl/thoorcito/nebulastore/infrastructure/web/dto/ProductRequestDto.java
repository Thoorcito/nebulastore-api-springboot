package cl.thoorcito.nebulastore.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequestDto(
    @Schema(description = "Codigo unico del producto", example = "FIL-PLA-001")
    @NotBlank(message = "El codigo es obligatorio") String code,

    @Schema(description = "Nombre del producto", example = "Filamento PLA 1kg")
    @NotBlank(message = "El nombre es obligatorio") String name,

    @Schema(description = "Tipo de producto", example = "FILAMENT", allowableValues = {"FILAMENT", "MACHINE", "CUSTOM_PRINT"})
    @NotBlank(message = "El tipo es obligatorio") String type,

    @Schema(description = "Precio unitario en CLP", example = "15000")
    @PositiveOrZero(message = "El precio no puede ser negativo") double unitPrice,

    @Schema(description = "Stock disponible", example = "20")
    @PositiveOrZero(message = "El stock no puede ser negativo") int stockAvailable
) {
}