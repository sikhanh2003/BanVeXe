package com.example.banvexe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.banvexe.repositories.RouteRepository;

import java.util.*;

@RestController
@RequestMapping("/api")
public class LocationController {

    @Autowired
    private RouteRepository routeRepository;

    // Lấy tất cả địa điểm (đi + đến)
    @GetMapping("/locations")
    public List<String> getAllLocations() {
        Set<String> locations = new HashSet<>();

        locations.addAll(routeRepository.findDistinctDepartureLocations());
        locations.addAll(routeRepository.findDistinctArrivalLocations());

        return new ArrayList<>(locations);
    }
}