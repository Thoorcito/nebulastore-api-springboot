package cl.thoorcito.nebulastore.infrastructure.web.dto;

public record ProductResponseDto(
    Long id,
    String code,
    String name,
    String type,
    double unitPrice,
    int stockAvailable
) {
}