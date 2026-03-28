package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.dto.response.AuditLogResponse;
import com.linhdv.efms_identity_service.mapper.AuditLogMapper;
import com.linhdv.efms_identity_service.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(auditLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByRecord(String tableName, UUID recordId, Pageable pageable) {
        return auditLogRepository.findByTableNameAndRecordId(tableName, recordId, pageable)
                .map(auditLogMapper::toResponse);
    }
}
