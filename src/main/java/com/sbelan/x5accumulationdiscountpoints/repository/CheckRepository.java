package com.sbelan.x5accumulationdiscountpoints.repository;

import com.sbelan.x5accumulationdiscountpoints.model.Check;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckRepository extends JpaRepository<Check, Long> {

    List<Check> findAllByClientId(Long clientId);
}
