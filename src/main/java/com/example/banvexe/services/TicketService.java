package com.example.banvexe.services;

import com.example.banvexe.models.entities.Ticket;
import java.util.List;
import com.example.banvexe.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}