package cl.thoorcito.nebulastore.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.thoorcito.nebulastore.infrastructure.persistence.ProductEntity;

// JpaRepository<ProductEntity, Long>: le dice a Spring Data "genera la
// implementacion de CRUD basico (save, findById, findAll, deleteById...)
// para esta entidad automaticamente, no escribas SQL a mano".
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    // Metodos "derivados": Spring Data lee el nombre del metodo y genera
    // la consulta SQL correspondiente solo con la firma, sin implementarla.
    Optional<ProductEntity> findByCode(String code);
    boolean existsByCode(String code);
}