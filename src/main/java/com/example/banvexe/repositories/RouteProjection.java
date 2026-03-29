package com.example.banvexe.repositories;

public interface RouteProjection {
    Long getId();
    String getDepartureLocation();
    String getArrivalLocation();
    Double getDistanceKm();
    Double getMinPrice(); // Spring sẽ tự tìm trường 'minPrice' trong câu Query bên dưới
}