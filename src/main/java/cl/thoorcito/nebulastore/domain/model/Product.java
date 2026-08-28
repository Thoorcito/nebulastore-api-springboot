package cl.thoorcito.nebulastore.domain.model;

// Record plano: solo datos, sin comportamiento ni validacion propia.
// (validar stock, validar dimensiones) vive en el ServiceImpl, no aca.
public record Product(
    Long id,
    String code,
    String name,
    String type,          // "FILAMENT" | "MACHINE" | "CUSTOM_PRINT"
    double unitPrice,
    int stockAvailable
) {
}