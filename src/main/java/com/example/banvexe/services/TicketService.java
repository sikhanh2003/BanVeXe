package com.example.banvexe.services;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public String holdSeat(Long ticketId) {
        return ticketRepository.findById(ticketId).map(ticket -> {
            
            LocalDateTime now = LocalDateTime.now();
            
            // 1. Kiểm tra trạng thái bằng Enum
            boolean isAvailable = Ticket.TicketStatus.AVAILABLE.equals(ticket.getStatus());
            boolean isHoldExpired = Ticket.TicketStatus.HOLD.equals(ticket.getStatus()) 
                                    && ticket.getHoldExpiresAt() != null 
                                    && ticket.getHoldExpiresAt().isBefore(now);

            // 2. Logic giữ ghế
            if (isAvailable || isHoldExpired) {
                ticket.setStatus(Ticket.TicketStatus.HOLD); // Gán trực tiếp bằng Enum
                ticket.setHoldExpiresAt(now.plusMinutes(10)); 
                ticketRepository.save(ticket);
                return "Giữ ghế thành công! Vui lòng thanh toán trong 10 phút.";
            }
            
            // 3. Phản hồi các trường hợp khác
            if (Ticket.TicketStatus.BOOKED.equals(ticket.getStatus()) || 
                Ticket.TicketStatus.PAID.equals(ticket.getStatus())) {
                return "Ghế này đã được bán hoặc thanh toán, vui lòng chọn ghế khác.";
            } else {
                return "Ghế này đang được người khác giữ.";
            }

        }).orElse("Lỗi: Không tìm thấy thông tin vé!");
    }

    @Transactional
    public void releaseExpiredTickets() {
        // Quét và giải phóng các ghế hết hạn giữ chỗ (HOLD -> AVAILABLE)
        List<Ticket> expiredTickets = ticketRepository.findAll()
            .stream()
            .filter(t -> Ticket.TicketStatus.HOLD.equals(t.getStatus()) 
                    && t.getHoldExpiresAt() != null 
                    && t.getHoldExpiresAt().isBefore(LocalDateTime.now()))
            .toList();
            
        expiredTickets.forEach(t -> {
            t.setStatus(Ticket.TicketStatus.AVAILABLE);
            t.setHoldExpiresAt(null);
            ticketRepository.save(t);
        });
    }
}