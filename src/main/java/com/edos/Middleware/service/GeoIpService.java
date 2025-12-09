package com.edos.Middleware.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@Service
public class GeoIpService {

    private DatabaseReader dbReader;

    @PostConstruct
    public void init() throws IOException {
        // The user should place GeoLite2-City.mmdb in src/main/resources
        ClassPathResource resource = new ClassPathResource("GeoLite2-City.mmdb");
        if (!resource.exists()) return;
        File database = resource.getFile();
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public Optional<double[]> lookupLatLon(String ip) {
        if (ip == null || dbReader == null) return Optional.empty();
        try {
            InetAddress address = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(address);
            if (response != null && response.getLocation() != null && response.getLocation().getLatitude() != null && response.getLocation().getLongitude() != null) {
                double lat = response.getLocation().getLatitude();
                double lon = response.getLocation().getLongitude();
                return Optional.of(new double[]{lat, lon});
            }
        } catch (IOException | GeoIp2Exception ignored) {
        }
        return Optional.empty();
    }
}
