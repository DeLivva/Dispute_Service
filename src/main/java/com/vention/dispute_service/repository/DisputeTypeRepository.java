package com.vention.dispute_service.repository;

import com.vention.dispute_service.domain.DisputeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisputeTypeRepository extends JpaRepository<DisputeTypeEntity, Long> {
}
