package cl.thoorcito.nebulastore.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.thoorcito.nebulastore.domain.exception.ResourceNotFoundException;
import cl.thoorcito.nebulastore.domain.model.Product;
import cl.thoorcito.nebulastore.infrastructure.persistence.ProductEntity;
import cl.thoorcito.nebulastore.infrastructure.persistence.repository.ProductJpaRepository;

// @Service: marca esta clase como un bean de logica de negocio,
// para que Spring la inyecte automaticamente donde se necesite.
@Service
public class ProductServiceImpl implements ProductService {

    // Inyectamos el repositorio JPA directo 
    private final ProductJpaRepository productJpaRepository;

    public ProductServiceImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    // @Transactional(readOnly = true): optimiza la consulta (Hibernate sabe
    // que no va a escribir nada) y evita que se abra una transaccion de escritura.
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        ProductEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id '" + id + "' not found"));
        return toDomain(entity);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        ProductEntity entity = new ProductEntity(
                product.code(), product.name(), product.type(),
                product.unitPrice(), product.stockAvailable());
        ProductEntity saved = productJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        ProductEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id '" + id + "' not found"));

        entity.setCode(product.code());
        entity.setName(product.name());
        entity.setType(product.type());
        entity.setUnitPrice(product.unitPrice());
        entity.setStockAvailable(product.stockAvailable());

        ProductEntity updated = productJpaRepository.save(entity);
        return toDomain(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productJpaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id '" + id + "' not found");
        }
        productJpaRepository.deleteById(id);
    }

    // Convierte la entidad JPA (detalle tecnico) al record de dominio (lo que
    // el resto de la app conoce). Este mapeo manual es la "frontera" entre
    // infraestructura y dominio.
    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(), entity.getCode(), entity.getName(),
                entity.getType(), entity.getUnitPrice(), entity.getStockAvailable());
    }
}