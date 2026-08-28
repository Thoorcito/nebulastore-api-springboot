package cl.thoorcito.nebulastore.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.thoorcito.nebulastore.application.service.ProductService;
import cl.thoorcito.nebulastore.domain.model.Product;
import cl.thoorcito.nebulastore.infrastructure.web.dto.ProductRequestDto;
import cl.thoorcito.nebulastore.infrastructure.web.dto.ProductResponseDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto request) {
        Product created = productService.createProduct(new Product(
                null, request.code(), request.name(), request.type(),
                request.unitPrice(), request.stockAvailable()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto request) {
        Product updated = productService.updateProduct(id, new Product(
                id, request.code(), request.name(), request.type(),
                request.unitPrice(), request.stockAvailable()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(product.id(), product.code(), product.name(),
                product.type(), product.unitPrice(), product.stockAvailable());
    }
}