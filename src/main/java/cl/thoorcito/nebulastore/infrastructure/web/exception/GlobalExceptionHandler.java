package cl.thoorcito.nebulastore.infrastructure.web.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import cl.thoorcito.nebulastore.domain.exception.ExceedsBuildVolumeException;
import cl.thoorcito.nebulastore.domain.exception.InvalidQuantityException;
import cl.thoorcito.nebulastore.domain.exception.OutOfStockException;
import cl.thoorcito.nebulastore.domain.exception.ResourceNotFoundException;
import cl.thoorcito.nebulastore.infrastructure.web.dto.ErrorResponse;

// @RestControllerAdvice: intercepta las excepciones lanzadas por CUALQUIER
// controller de la app, de forma centralizada. Sin esto, cualquier excepcion
// no capturada le devuelve al cliente un 500 con stacktrace crudo.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de @Valid en los DTOs (campos vacios, numeros negativos, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuantity(InvalidQuantityException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ExceedsBuildVolumeException.class)
    public ResponseEntity<ErrorResponse> handleExceedsVolume(ExceedsBuildVolumeException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Captura reglas de negocio sueltas lanzadas con IllegalArgumentException
    // (por ejemplo "el pedido debe tener al menos un item").
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    
    // Captura JSON mal formado, campos con tipo incorrecto, o numeros que
    // desbordan el tipo esperado (ej: un int recibiendo un numero gigante).
    // Ocurre a nivel de deserializacion, antes de que Bean Validation
    // (@Valid) o tu logica de negocio siquiera se ejecuten.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion es invalido o esta mal formado");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), message, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}