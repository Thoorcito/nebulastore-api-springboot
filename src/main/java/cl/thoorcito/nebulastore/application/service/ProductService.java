package cl.thoorcito.nebulastore.application.service;

import java.util.List;

import cl.thoorcito.nebulastore.domain.model.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}