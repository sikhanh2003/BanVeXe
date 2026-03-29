package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByTicketId(Long ticketId);

}