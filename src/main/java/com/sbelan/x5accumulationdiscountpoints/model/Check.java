package com.sbelan.x5accumulationdiscountpoints.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@Entity
@Table(name = "check_")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Check extends BaseEntity {

    @Size(max = 20)
    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "check_sum")
    private BigDecimal checkSum;

    @OneToMany(mappedBy = "check")
    @Exclude
    private List<CheckPosition> checkPositions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

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
        Check check = (Check) o;
        return Objects.equals(cardNumber, check.cardNumber) && Objects.equals(
            checkSum, check.checkSum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardNumber, checkSum);
    }
}
