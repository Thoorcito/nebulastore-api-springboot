package cl.thoorcito.nebulastore.infrastructure.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record OrderRequestDto(
    @NotBlank(message = "El nombre del cliente es obligatorio") String customerName,
    // @Valid en cascada: valida tambien cada OrderItemRequestDto de la lista,
    // no solo que la lista no este vacia.
    @NotEmpty(message = "El pedido debe tener al menos un item") @Valid List<OrderItemRequestDto> items
) {
}