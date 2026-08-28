package cl.thoorcito.nebulastore.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

// @NotBlank/@PositiveOrZero: Bean Validation. Si el request no cumple estas
// reglas, Spring lanza MethodArgumentNotValidException ANTES de que el
// controller siquiera reciba el objeto - el GlobalExceptionHandler la
// traduce a un 400 con el detalle del campo que fallo.
public record ProductRequestDto(
    @NotBlank(message = "El codigo es obligatorio") String code,
    @NotBlank(message = "El nombre es obligatorio") String name,
    @NotBlank(message = "El tipo es obligatorio") String type,
    @PositiveOrZero(message = "El precio no puede ser negativo") double unitPrice,
    @PositiveOrZero(message = "El stock no puede ser negativo") int stockAvailable
) {
}