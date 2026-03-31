package com.example.banvexe.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import com.example.banvexe.models.entities.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // FIX N+1
    @EntityGraph(attributePaths = {"route", "bus"})
    Page<Trip> findAll(Pageable pageable);

    Page<Trip> findByRouteId(Long routeId, Pageable pageable);

    List<Trip> findByRouteDepartureLocationAndRouteArrivalLocationAndDepartureTimeBetween(
            String departure,
            String arrival,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT t FROM Trip t WHERE FUNCTION('DATE', t.departureTime) = :date")
    Page<Trip> findByDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT SUM(b.capacity) FROM Trip t JOIN t.bus b")
    Long getTotalSystemCapacity();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trip t WHERE t.id = :id")
    Optional<Trip> findByIdForUpdate(@Param("id") Long id);
}