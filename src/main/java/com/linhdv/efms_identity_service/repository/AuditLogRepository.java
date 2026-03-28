package com.linhdv.efms_identity_service.repository;

import com.linhdv.efms_identity_service.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByTableNameAndRecordId(String tableName, UUID recordId, Pageable pageable);
}
