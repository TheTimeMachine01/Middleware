package com.edos.Middleware.repository;

import com.edos.Middleware.entity.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, UUID>, JpaSpecificationExecutor<SecurityAlert> {

}
