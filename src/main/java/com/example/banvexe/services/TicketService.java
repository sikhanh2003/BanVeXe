package com.example.banvexe.services;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    }
}