package com.vention.dispute_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "disputes")
public class DisputeEntity extends BaseEntity {
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "type_id", foreignKey = @ForeignKey(name = "fk_dispute_type"))
    private DisputeTypeEntity type;

    @Column(name = "description")
    private String description;
}
