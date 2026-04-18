package com.linhdv.efms_identity_service.repository;

import com.linhdv.efms_identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    java.util.List<User> findAllByIdInAndCompanyId(java.util.Collection<UUID> ids, UUID companyId);
}
