package com.edos.Middleware.controller;

import com.edos.Middleware.entity.SecurityAlert;
import com.edos.Middleware.repository.SecurityAlertRepository;
import com.edos.Middleware.service.GeoIpService;
import com.edos.Middleware.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @Autowired
    private SecurityAlertRepository alertRepository;

    @Autowired
    private GeoIpService geoIpService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping("/traffic/real-time")
    public ResponseEntity<Map<String, Object>> getRealTimeTraffic() {
        Long userId = securityUtils.getCurrentUserId();
        
        // Fetch recent alerts for the user to generate "threat points"
        List<SecurityAlert> recentAlerts = alertRepository.findAll((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("userId"), userId),
                cb.greaterThan(root.get("detectedAt"), Instant.now().minusSeconds(3600)) // last hour
            )
        );

        List<Map<String, Object>> arcs = new ArrayList<>();
        List<Map<String, Object>> points = new ArrayList<>();

        for (SecurityAlert alert : recentAlerts) {
            if (alert.getSourceIp() == null) continue;
            
            Optional<double[]> sourceCoords = geoIpService.lookupLatLon(alert.getSourceIp());
            sourceCoords.ifPresent(coords -> {
                Map<String, Object> point = new HashMap<>();
                point.put("lat", coords[0]);
                point.put("lng", coords[1]);
                point.put("label", alert.getSourceIp() + " - " + alert.getAttackType());
                point.put("isAttack", true);
                points.add(point);

                // Create an arc if we have target data or a default target
                Map<String, Object> arc = new HashMap<>();
                arc.put("id", alert.getId().toString());
                arc.put("startLat", coords[0]);
                arc.put("startLng", coords[1]);
                arc.put("endLat", 0.0); // Default to center for visualization if target unknown
                arc.put("endLng", 0.0);
                arc.put("isAttack", true);
                arc.put("timestamp", alert.getDetectedAt());
                arcs.add(arc);
            });
        }

        Map<String, Object> response = new HashMap<>();
        response.put("arcs", arcs);
        response.put("points", points);
        response.put("timestamp", Instant.now());
        response.put("total_connections", arcs.size() * 5); // Simulated relative scale
        response.put("active_threats", recentAlerts.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/threats/locations")
    public ResponseEntity<List<Map<String, Object>>> getThreatLocations() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
            alertRepository.findAll((root, query, cb) -> cb.equal(root.get("userId"), userId))
                .stream()
                .map(alert -> {
                    Map<String, Object> loc = new HashMap<>();
                    geoIpService.lookupLatLon(alert.getSourceIp()).ifPresent(coords -> {
                        loc.put("lat", coords[0]);
                        loc.put("lng", coords[1]);
                    });
                    loc.put("ip", alert.getSourceIp());
                    loc.put("severity", alert.getSeverity());
                    return loc;
                })
                .filter(m -> m.containsKey("lat"))
                .collect(Collectors.toList())
        );
    }
}
