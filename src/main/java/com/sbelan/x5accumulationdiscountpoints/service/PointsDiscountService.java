package com.sbelan.x5accumulationdiscountpoints.service;

import com.sbelan.x5accumulationdiscountpoints.model.api.NewCheckRequest;

public interface PointsDiscountService {

    Long findPointsQuantityByClientId(Long clientId);

    Long adjustPointsQuantity(NewCheckRequest check);
}
