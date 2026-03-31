package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.SeatHold;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
    List<SeatHold> findByTripAndExpiresAtAfter(Trip trip, LocalDateTime now);

    List<SeatHold> findByTripAndUserAndExpiresAtAfter(Trip trip, User user, LocalDateTime now);

    Optional<SeatHold> findByTripAndSeatNumberAndExpiresAtAfter(Trip trip, String seatNumber, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
