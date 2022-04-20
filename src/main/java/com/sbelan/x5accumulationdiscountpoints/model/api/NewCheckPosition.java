package com.sbelan.x5accumulationdiscountpoints.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewCheckPosition {

    @NotNull
    private BigDecimal positionSum;
}
