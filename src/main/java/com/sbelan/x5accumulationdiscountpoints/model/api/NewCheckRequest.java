package com.sbelan.x5accumulationdiscountpoints.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewCheckRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private NewCheck check;
}
