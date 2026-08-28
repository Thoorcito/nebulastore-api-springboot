package cl.thoorcito.nebulastore.infrastructure.web.dto;

import java.util.List;

public record OrderResponseDto(
    Long id,
    String code,
    String customerName,
    String status,
    double total,
    List<OrderItemResponseDto> items
) {
}