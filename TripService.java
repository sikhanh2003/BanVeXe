package com.huy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepository;

    public List<Trip> searchTrips(String from, String to, String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        return tripRepository.findByOriginAndDestinationAndDepartureTimeBetween(from, to, startOfDay, endOfDay);
    }
}