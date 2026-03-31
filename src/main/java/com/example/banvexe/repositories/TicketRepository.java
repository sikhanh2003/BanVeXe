package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.models.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.banvexe.models.entities.User;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);

    // Tính tổng doanh thu
    @Query("SELECT SUM(tr.pricePerTicket) FROM Ticket t JOIN t.trip tr WHERE t.status = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT t FROM Ticket t JOIN FETCH t.trip tr JOIN FETCH tr.route WHERE t.user = :user")
    List<Ticket> findByUserWithTrip(@Param("user") User user);

    @Query("""
                SELECT t FROM Ticket t
                JOIN FETCH t.user
                JOIN FETCH t.trip tr
                JOIN FETCH tr.route
                JOIN FETCH tr.bus
            """)
    List<Ticket> findAllWithDetails();

    List<Ticket> findByUserAndStatusIn(User user, List<Ticket.TicketStatus> statuses);

    List<Ticket> findByTrip(Trip trip);

    List<Ticket> findByTripAndStatusIn(Trip trip, List<Ticket.TicketStatus> statuses);

    @Query("SELECT t FROM Ticket t WHERE t.trip = :trip AND t.status = 'AVAILABLE' AND t.holdExpiresAt > :now")
    List<Ticket> findActiveHoldsByTrip(@Param("trip") Trip trip, @Param("now") LocalDateTime now);

    @Query("""
            SELECT t FROM Ticket t
            WHERE t.trip = :trip
              AND t.user = :user
              AND t.status = 'AVAILABLE'
              AND t.holdExpiresAt > :now
            """)
    List<Ticket> findActiveHoldsByTripAndUser(@Param("trip") Trip trip, @Param("user") User user,
            @Param("now") LocalDateTime now);

}