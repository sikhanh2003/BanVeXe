package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.models.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Kiểm tra ghế đã được đặt chưa
    boolean existsByTripAndSeatNumber(Trip trip, Integer seatNumber);

    // Tính tổng doanh thu
    @Query("SELECT SUM(tr.pricePerTicket) FROM Ticket t JOIN t.trip tr WHERE t.status = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT t FROM Ticket t JOIN FETCH t.trip tr JOIN FETCH t.user u ORDER BY t.id DESC")
    List<Ticket> findAllWithDetails();

    
}