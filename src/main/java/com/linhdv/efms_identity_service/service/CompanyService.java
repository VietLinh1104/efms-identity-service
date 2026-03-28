package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.dto.request.CompanyRequest;
import com.linhdv.efms_identity_service.dto.response.CompanyResponse;
import com.linhdv.efms_identity_service.entity.Company;
import com.linhdv.efms_identity_service.mapper.CompanyMapper;
import com.linhdv.efms_identity_service.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return companyMapper.toResponse(company);
    }

    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = companyMapper.toEntity(request);
        company.setId(UUID.randomUUID());
        company.setCreatedAt(Instant.now());
        company = companyRepository.save(company);
        return companyMapper.toResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(UUID id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        companyMapper.updateEntity(request, company);
        company = companyRepository.save(company);
        return companyMapper.toResponse(company);
    }

    @Transactional
    public void deleteCompany(UUID id) {
        companyRepository.deleteById(id);
    }
}
