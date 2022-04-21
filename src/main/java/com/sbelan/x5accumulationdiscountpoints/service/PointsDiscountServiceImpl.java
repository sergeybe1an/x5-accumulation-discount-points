package com.sbelan.x5accumulationdiscountpoints.service;

import com.sbelan.x5accumulationdiscountpoints.exception.PointsProcessingException;
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
    private final static BigDecimal WITHDRAWAL_POINTS_10 = BigDecimal.valueOf(10);

    private ClientRepository clientRepository;
    private CheckRepository checkRepository;
    private CheckPositionRepository checkPositionRepository;

    @Override
    public Long findPointsQuantityByClientId(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new HttpClientErrorException(
                HttpStatus.NOT_FOUND, String.format("Client %d didn't found!", clientId)));

        log.info("PointsDiscountServiceImpl.findPointsQuantityByClientId - client: {}", client);

        return client.getAvailablePoints();
    }

    @Transactional
    @Override
    public Long adjustPointsQuantity(NewCheckRequest newCheck) throws PointsProcessingException {

        Client client = clientRepository.findById(newCheck.getClientId())
            .orElseThrow(() -> new HttpClientErrorException(
                HttpStatus.NOT_FOUND, String.format("Client %d didn't found!", newCheck.getClientId())));

        log.info("PointsDiscountServiceImpl.adjustPointsQuantity - client: {}", client);

        List<Check> checks = checkRepository.findAllByClientId(client.getId());

        //Корректировка баллов
        pointsCalculation(client, checks, newCheck);

        //Обновление данных
        client = clientRepository.save(client);
        Check newCheckEntity = checkMapping(newCheck.getCheck(), client);
        newCheckEntity = checkRepository.save(newCheckEntity);
        List<CheckPosition> newCheckPositionEntities = checkPositionMapping(newCheck.getCheck().getCheckPositions(), newCheckEntity);
        checkPositionRepository.saveAll(newCheckPositionEntities);

        return client.getAvailablePoints();
    }

    private Client pointsCalculation(Client client, List<Check> checks, NewCheckRequest newCheck) throws PointsProcessingException {
        BigDecimal checksSum = checks.stream()
            .filter(Objects::nonNull)
            .map(Check::getCheckSum)
            .reduce(newCheck.getCheck().getCheckSum(), BigDecimal::add);

        Long withdrawalPointsQuantity = withdrawalPointsQuantity(newCheck.getCheck());
        if (withdrawalPointsQuantity < 0) {
            throw new PointsProcessingException(
                String.format("Not enough points on client %d account! Need atleast %d",
                    newCheck.getClientId(),
                    Math.abs(withdrawalPointsQuantity)));
        }

        Long newPointsQuantity = newPointsQuantity(checksSum, newCheck.getCheck().getCheckSum());
        if (newPointsQuantity != null && newPointsQuantity > 0L) {
            client.setAvailablePoints(client.getAvailablePoints() - withdrawalPointsQuantity + newPointsQuantity);
        } else {
            client.setAvailablePoints(client.getAvailablePoints() - withdrawalPointsQuantity);
        }
        return client;
    }

    /**
     * Снятие баллов
     * */
    private Long withdrawalPointsQuantity(NewCheck check) {
        BigDecimal positionSum = check.getCheckPositions()
            .stream()
            .filter(Objects::nonNull)
            .map(NewCheckPosition::getPositionSum)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long result = 0;
        if (positionSum.compareTo(check.getCheckSum()) < 0) {
            BigDecimal diff = positionSum.subtract(check.getCheckSum());
            result = diff.divide(WITHDRAWAL_POINTS_10, 2, RoundingMode.DOWN).longValue();
        }

        return result;
    }

    /**
     * Начисление баллов
     */
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

    private Check checkMapping(NewCheck newCheck, Client client) {
        Check check = new Check();
        check.setCheckSum(newCheck.getCheckSum());
        check.setCardNumber(newCheck.getCardNumber());
        check.setClient(client);
        check.setCreated(LocalDateTime.now());
        check.setUpdated(LocalDateTime.now());

        return check;
    }

    private List<CheckPosition> checkPositionMapping(List<NewCheckPosition> newCheckPositions, Check check) {

        return newCheckPositions
            .stream()
            .filter(Objects::nonNull)
            .map(newCheckPosition -> {
                    CheckPosition checkPosition = new CheckPosition();
                    checkPosition.setCheck(check);
                    checkPosition.setPositionSum(newCheckPosition.getPositionSum());
                    checkPosition.setCreated(LocalDateTime.now());
                    checkPosition.setUpdated(LocalDateTime.now());
                    return checkPosition;
            })
            .collect(Collectors.toList());
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setCheckRepository(CheckRepository checkRepository) {
        this.checkRepository = checkRepository;
    }

    @Autowired
    public void setCheckPositionRepository(CheckPositionRepository checkPositionRepository) {
        this.checkPositionRepository = checkPositionRepository;
    }
}
