package com.vention.dispute_service.repository;

import com.vention.dispute_service.domain.DisputeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<DisputeEntity, Long>, PagingAndSortingRepository<DisputeEntity, Long> {
    List<DisputeEntity> findByUserId(Long userId);

    Boolean existsByOrderId(Long orderId);
}
