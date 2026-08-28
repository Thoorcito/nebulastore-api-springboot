package cl.thoorcito.nebulastore.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.thoorcito.nebulastore.domain.exception.ResourceNotFoundException;
import cl.thoorcito.nebulastore.domain.model.Product;
import cl.thoorcito.nebulastore.infrastructure.persistence.ProductEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.ProductJpaRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts_returnsMappedList() {
        // Arrange
        ProductEntity entity = new ProductEntity(1L, "FIL-001", "PLA", "FILAMENT", 15000, 10);
        when(productJpaRepository.findAll()).thenReturn(List.of(entity));

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("FIL-001", result.get(0).code());
    }

    @Test
    void getProductById_found_returnsProduct() {
        // Arrange
        ProductEntity entity = new ProductEntity(1L, "FIL-001", "PLA", "FILAMENT", 15000, 10);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertEquals("PLA", result.name());
    }

    @Test
    void getProductById_notFound_throwsException() {
        // Arrange
        when(productJpaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        // Aca Act y Assert quedan juntos porque lo que estamos probando es
        // justamente que la llamada lance una excepcion (no hay un "resultado"
        // que evaluar despues, el comportamiento esperado ES la excepcion).
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_savesAndReturnsProduct() {
        // Arrange
        Product input = new Product(null, "FIL-002", "ABS", "FILAMENT", 18000, 5);
        ProductEntity saved = new ProductEntity(2L, "FIL-002", "ABS", "FILAMENT", 18000, 5);
        when(productJpaRepository.save(any(ProductEntity.class))).thenReturn(saved);

        // Act
        Product result = productService.createProduct(input);

        // Assert
        assertEquals(2L, result.id());
        verify(productJpaRepository).save(any(ProductEntity.class));
    }

    @Test
    void deleteProduct_notFound_throwsException() {
        // Arrange
        when(productJpaRepository.existsById(5L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(5L));
    }
}