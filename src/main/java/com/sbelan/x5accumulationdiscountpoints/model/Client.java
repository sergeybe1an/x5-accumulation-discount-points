package com.sbelan.x5accumulationdiscountpoints.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@Entity
@Table(name = "client")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Client extends BaseEntity {

    @NotBlank
    @Size(max = 20)
    @Column(name = "card_number")
    private String cardNumber;
    @Column(name = "available_points")
    private Long availablePoints;
    @OneToMany(mappedBy = "client")
    @Exclude
    private Set<Check> checks = new HashSet<>();


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
        Client client = (Client) o;
        return Objects.equals(cardNumber, client.cardNumber) && Objects.equals(
            availablePoints, client.availablePoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardNumber, availablePoints);
    }
}
