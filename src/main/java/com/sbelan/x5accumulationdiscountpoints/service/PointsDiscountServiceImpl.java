package com.sbelan.x5accumulationdiscountpoints.service;

import com.sbelan.x5accumulationdiscountpoints.model.Check;
import com.sbelan.x5accumulationdiscountpoints.model.CheckPosition;
import com.sbelan.x5accumulationdiscountpoints.model.Client;
import com.sbelan.x5accumulationdiscountpoints.model.api.NewCheck;
import com.sbelan.x5accumulationdiscountpoints.model.api.NewCheckPosition;
import com.sbelan.x5accumulationdiscountpoints.model.api.NewCheckRequest;
import com.sbelan.x5accumulationdiscountpoints.repository.CheckPositionRepository;
import com.sbelan.x5accumulationdiscountpoints.repository.CheckRepository;
import com.sbelan.x5accumulationdiscountpoints.repository.ClientRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class PointsDiscountServiceImpl implements PointsDiscountService {

    private final static BigDecimal CHECK_SUM_50_000 = BigDecimal.valueOf(50_000);
    private final static BigDecimal CHECK_SUM_100_000 = BigDecimal.valueOf(100_000);
    private final static BigDecimal ACCRUAL_POINTS_50 = BigDecimal.valueOf(50);
    private final static BigDecimal ACCRUAL_POINTS_40 = BigDecimal.valueOf(40);
    private final static BigDecimal ACCRUAL_POINTS_30 = BigDecimal.valueOf(30);

    private ClientRepository clientRepository;
    private CheckRepository checkRepository;

    @Override
    public Long findPointsQuantityByClientId(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new HttpClientErrorException(
                HttpStatus.NOT_FOUND, String.format("Client %d didn't found!", clientId)));

        log.info("PointsDiscountServiceImpl.findPointsQuantityByClientId - client: {} found by id: {}", client, clientId);

        return client.getAvailablePoints();
    }

    @Transactional
    @Override
    public Long adjustPointsQuantity(NewCheckRequest newCheck) {

        Client client = clientRepository.findById(newCheck.getClientId())
            .orElseThrow(() -> new HttpClientErrorException(
                HttpStatus.NOT_FOUND, String.format("Client %d didn't found!", newCheck.getClientId())));

        List<Check> checks = checkRepository.findAllByClientId(client.getId());

        BigDecimal checksSum = checks.stream()
            .filter(Objects::nonNull)
            .map(Check::getCheckSum)
            .reduce(newCheck.getCheck().getCheckSum(), BigDecimal::add);

        Long newPointsQuantity = newPointsQuantity(checksSum, newCheck.getCheck().getCheckSum());
        if (newPointsQuantity != null && newPointsQuantity > 0L) {
            client.setAvailablePoints(client.getAvailablePoints() + newPointsQuantity);
            client = clientRepository.save(client);
        }

        Check newCheckEntity = checkMapping(newCheck.getCheck(), client);
        checkRepository.save(newCheckEntity);

        return client.getAvailablePoints();
    }

    private Check checkMapping(NewCheck newCheck, Client client) {
        Check check = new Check();
        check.setCheckSum(newCheck.getCheckSum());
        check.setCardNumber(newCheck.getCardNumber());
        check.setClient(client);
        check.setCreated(LocalDateTime.now());
        check.setUpdated(LocalDateTime.now());

        Set<CheckPosition> checkPositions = newCheck.getCheckPositions()
            .stream()
            .filter(Objects::nonNull)
            .map(this::checkPositionMapping)
            .collect(Collectors.toSet());

        check.setCheckPositions(checkPositions);

        return check;
    }

    private CheckPosition checkPositionMapping(NewCheckPosition newCheckPosition) {
        CheckPosition checkPosition = new CheckPosition();
        checkPosition.setPositionSum(newCheckPosition.getPositionSum());
        checkPosition.setCreated(LocalDateTime.now());
        checkPosition.setUpdated(LocalDateTime.now());

        return checkPosition;
    }

    private Long newPointsQuantity(BigDecimal checksSum, BigDecimal newCheckSum) {

        newCheckSum = newCheckSum.setScale(0, RoundingMode.HALF_UP);

        long pointsQuantity = 0L;
        if (checksSum.compareTo(CHECK_SUM_50_000) <= 0) {
            pointsQuantity = newCheckSum.divide(ACCRUAL_POINTS_50,2, RoundingMode.DOWN).longValue();
        } else if (checksSum.compareTo(CHECK_SUM_50_000) > 0 && checksSum.compareTo(CHECK_SUM_100_000) <= 0) {
            pointsQuantity = newCheckSum.divide(ACCRUAL_POINTS_40,2, RoundingMode.DOWN).longValue();
        } else if (checksSum.compareTo(CHECK_SUM_100_000) > 0) {
            pointsQuantity = newCheckSum.divide(ACCRUAL_POINTS_30,2, RoundingMode.DOWN).longValue();
        }

        return pointsQuantity;
    }

    private void withdrawalOfPoints() {

    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setCheckRepository(CheckRepository checkRepository) {
        this.checkRepository = checkRepository;
    }
}
