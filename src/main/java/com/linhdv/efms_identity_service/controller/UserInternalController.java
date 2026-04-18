package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.response.UserInternalResponse;
import com.linhdv.efms_identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    @PostMapping("/batch")
    public ApiResponse<List<UserInternalResponse>> getUsersBatch(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestBody Set<UUID> userIds) {
        return ApiResponse.success(userService.getUsersBatch(userIds, companyId));
    }
}
