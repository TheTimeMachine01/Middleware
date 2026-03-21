package com.edos.Middleware.controller;

import com.edos.Middleware.config.SecurityUtils;
import com.edos.Middleware.dto.SecurityAlertDto;
import com.edos.Middleware.dto.SeverityCount;
import com.edos.Middleware.service.AlertService;
import com.edos.Middleware.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AlertGraphQlController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private MonitoringService monitoringService;

    @QueryMapping
    public List<SecurityAlertDto> listAlerts(
            @Argument String level,
            @Argument Boolean read,
            @Argument Integer limit,
            @Argument Integer offset) {
        
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        int finalLimit = (limit != null && limit > 0) ? limit : 50;
        int finalOffset = (offset != null && offset >= 0) ? offset : 0;
        int pageNumber = finalOffset / finalLimit;
        
        Pageable pageable = PageRequest.of(pageNumber, finalLimit, Sort.by(Sort.Direction.DESC, "detectedAt"));
        return alertService.findAlertsForUser(userId, level, read, pageable).getContent();
    }

    @QueryMapping
    public SecurityAlertDto getAlertById(@Argument UUID id) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        return alertService.findAlertById(id, userId).orElse(null);
    }

    @QueryMapping
    public Map<String, Object> getAlertStats() {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        Map<String, Object> stats = alertService.getAlertStats(userId);
        
        // Safely extract and convert the map to a List of SeverityCount records
        Object bySeverityObj = stats.get("by_severity");
        if (bySeverityObj instanceof Map<?, ?> severityMap) {
            List<SeverityCount> severityList = severityMap.entrySet().stream()
                    .map(entry -> new SeverityCount(
                            String.valueOf(entry.getKey()), 
                            ((Number) entry.getValue()).longValue()
                    ))
                    .toList();
            stats.put("by_severity", severityList);
        }
        
        return stats;
    }

    @QueryMapping
    public com.edos.Middleware.dto.MonitoringData getSystemMetrics() {
        return monitoringService.getLatestMetrics();
    }

    @MutationMapping
    public Boolean markAsRead(@Argument UUID id) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        return alertService.markAsRead(id, userId);
    }

    @MutationMapping
    public Boolean markAllRead() {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        alertService.markAllAsRead(userId);
        return true;
    }

    @MutationMapping
    public Boolean deleteAlert(@Argument UUID id) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        return alertService.deleteAlert(id, userId);
    }
}
