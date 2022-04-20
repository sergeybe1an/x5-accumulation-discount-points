package com.sbelan.x5accumulationdiscountpoints.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewCheck {

    @NotBlank
    @Size(max = 20)
    private String cardNumber;
    @NotNull
    private BigDecimal checkSum;
    private Set<NewCheckPosition> checkPositions = new HashSet<>();
}
