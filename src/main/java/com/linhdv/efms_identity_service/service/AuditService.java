package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.entity.AuditLog;
import com.linhdv.efms_identity_service.repository.AuditLogRepository;
import com.linhdv.efms_identity_service.repository.UserRepository;
import com.linhdv.efms_identity_service.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logChange(String tableName, UUID recordId, String action, Map<String, Object> oldData,
            Map<String, Object> newData) {
        AuditLog log = new AuditLog();
        log.setId(UUID.randomUUID());
        log.setTableName(tableName);
        log.setRecordId(recordId);
        log.setAction(action);
        log.setOldData(oldData);
        log.setNewData(newData);
        log.setChangedAt(Instant.now());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UUID userId = ((UserDetailsImpl) principal).getId();
            userRepository.findById(userId).ifPresent(log::setChangedBy);
        }

        auditLogRepository.save(log);
    }
}
