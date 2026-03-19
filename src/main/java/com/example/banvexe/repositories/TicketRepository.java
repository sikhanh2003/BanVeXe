package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Bạn có thể thêm các hàm tìm kiếm theo user_id nếu cần sau này
    @Query("SELECT SUM(tr.pricePerTicket) FROM Ticket t JOIN t.trip tr WHERE t.status = 'PAID'")
    Double getTotalRevenue();

}