package cl.thoorcito.nebulastore.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.thoorcito.nebulastore.infrastructure.persistence.OrderItemEntity;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderId(Long orderId);
}