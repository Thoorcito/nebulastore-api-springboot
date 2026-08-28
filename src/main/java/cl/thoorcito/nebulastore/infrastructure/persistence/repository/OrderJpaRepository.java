package cl.thoorcito.nebulastore.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.thoorcito.nebulastore.infrastructure.persistence.OrderEntity;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByCode(String code);
    boolean existsByCode(String code);
}