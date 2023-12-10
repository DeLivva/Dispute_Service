package com.vention.dispute_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "dispute_type")
@Getter
@Setter
public class DisputeTypeEntity extends BaseEntity {
    @Column(name = "name")
    private String name;
}