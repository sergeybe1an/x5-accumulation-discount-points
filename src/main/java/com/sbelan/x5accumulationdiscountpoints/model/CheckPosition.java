package com.sbelan.x5accumulationdiscountpoints.model;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "check_position")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CheckPosition extends BaseEntity {

    @Column(name = "position_sum")
    private BigDecimal positionSum;
    @ManyToOne
    @JoinColumn(name = "check_id", nullable = false)
    private Check check;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CheckPosition that = (CheckPosition) o;
        return Objects.equals(positionSum,
            that.positionSum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), positionSum);
    }
}
