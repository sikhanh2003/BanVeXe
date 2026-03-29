package com.example.banvexe.services;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Page<Ticket> getAllTicketsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ticketRepository.findAll(pageable);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets;
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

    public boolean deleteTicket(Long id) {
        // Kiểm tra xem vé có tồn tại không trước khi xóa
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            ticketRepository.deleteById(id);
            return true; // Xóa thành công
        }
        return false; // Không tìm thấy vé để xóa
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }
}