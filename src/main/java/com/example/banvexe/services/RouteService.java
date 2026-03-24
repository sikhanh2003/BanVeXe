package com.example.banvexe.services;

import com.example.banvexe.models.entities.Route;
import com.example.banvexe.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElseThrow();
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    // Trong RouteService.java
    public Route updateRoute(Long id, Route newRouteData) {
        Route existingRoute = routeRepository.findById(id).orElseThrow();
        existingRoute.setDepartureLocation(newRouteData.getDepartureLocation());
        existingRoute.setArrivalLocation(newRouteData.getArrivalLocation());
        existingRoute.setDistanceKm(newRouteData.getDistanceKm());
        return routeRepository.save(existingRoute);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    
}