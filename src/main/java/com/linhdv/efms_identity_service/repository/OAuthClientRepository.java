package com.linhdv.efms_identity_service.repository;

import com.linhdv.efms_identity_service.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, UUID> {
    Optional<OAuthClient> findByClientId(String clientId);
}
