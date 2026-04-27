package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.CartAuditLog;
import com.infy.ekart.cartservice.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartAuditLogRepository extends JpaRepository<CartAuditLog, UUID> {

    List<CartAuditLog> findByCartIdOrderByTimestampDesc(UUID cartId);

    List<CartAuditLog> findByCartIdAndAction(UUID cartId, AuditAction action);

    List<CartAuditLog> findTop10ByCartIdOrderByTimestampDesc(UUID cartId);
}