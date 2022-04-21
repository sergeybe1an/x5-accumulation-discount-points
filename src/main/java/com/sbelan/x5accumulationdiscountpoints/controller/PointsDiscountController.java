package com.sbelan.x5accumulationdiscountpoints.controller;

import com.sbelan.x5accumulationdiscountpoints.exception.PointsProcessingException;
import com.sbelan.x5accumulationdiscountpoints.model.api.NewCheckRequest;
import com.sbelan.x5accumulationdiscountpoints.service.PointsDiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/discount-points")
@Tag(name = "Points discount controller", description = "Points discount controller")
public class PointsDiscountController {

    private PointsDiscountService pointsDiscountService;

    @GetMapping("/{clientId}")
    @Operation(summary = "Get points quantity by clientId")
    public ResponseEntity<Long> getPointsQuantityByClientId(@PathVariable Long clientId) {

        Long pointsQuantity;
        try {
            pointsQuantity = pointsDiscountService.findPointsQuantityByClientId(clientId);
            log.info("PointsDiscountController.getPointsQuantityByClientId for clientId: {}, current points quantity: {}", clientId, pointsQuantity);
        } catch (HttpClientErrorException e) {
            log.error("PointsDiscountController.getPointsQuantityByClientId clientId: {}, error: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("PointsDiscountController.getPointsQuantityByClientId clientId: {}, error: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(pointsQuantity);
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust points quantity")
    public ResponseEntity<Map<Object, Object>> adjustPointsQuantity(@Valid @RequestBody NewCheckRequest check) {

        Map<Object, Object> response = new HashMap<>();
        try {
            Long newPointsQuantity = pointsDiscountService.adjustPointsQuantity(check);
            response.put("points_quantity", newPointsQuantity);
            log.info("PointsDiscountController.adjustPointsQuantity for check: {}, new points quantity: {}", check, newPointsQuantity);
        } catch (PointsProcessingException e) {
            log.error("PointsDiscountController.adjustPointsQuantity point processing exception: {}", e.getMessage());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (HttpClientErrorException e) {
            log.error("PointsDiscountController.adjustPointsQuantity check: {}, error: {}", check, e.getMessage());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("PointsDiscountController.adjustPointsQuantity check: {}, error: {}", check, e.getMessage());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @Autowired
    public void setPointsDiscountService(PointsDiscountService pointsDiscountService) {
        this.pointsDiscountService = pointsDiscountService;
    }
}
