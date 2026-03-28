package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.response.AuditLogResponse;
import com.linhdv.efms_identity_service.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<Page<AuditLogResponse>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(auditLogService.getAllLogs(PageRequest.of(page, size)));
    }

    @GetMapping("/record")
    public ApiResponse<Page<AuditLogResponse>> getLogsByRecord(
            @RequestParam String tableName,
            @RequestParam UUID recordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(auditLogService.getLogsByRecord(tableName, recordId, PageRequest.of(page, size)));
    }
}
