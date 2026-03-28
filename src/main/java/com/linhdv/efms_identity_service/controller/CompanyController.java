package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.request.CompanyRequest;
import com.linhdv.efms_identity_service.dto.response.CompanyResponse;
import com.linhdv.efms_identity_service.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ApiResponse<List<CompanyResponse>> getAllCompanies() {
        return ApiResponse.success(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getCompanyById(@PathVariable UUID id) {
        return ApiResponse.success(companyService.getCompanyById(id));
    }

    @PostMapping
    public ApiResponse<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success("Company created successfully", companyService.createCompany(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success("Company updated successfully", companyService.updateCompany(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ApiResponse.success("Company deleted successfully");
    }
}
