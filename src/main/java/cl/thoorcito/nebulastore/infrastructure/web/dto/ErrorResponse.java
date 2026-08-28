package cl.thoorcito.nebulastore.infrastructure.web.dto;

import java.time.LocalDateTime;

// DTO unificado que devuelve el GlobalExceptionHandler ante cualquier error.
public record ErrorResponse(int code, String message, LocalDateTime timestamp) {
}