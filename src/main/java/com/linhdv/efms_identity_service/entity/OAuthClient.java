package com.linhdv.efms_identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "oauth_clients", schema = "identity")
public class OAuthClient {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Size(max = 255)
    @NotNull
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @Size(max = 1000)
    @NotNull
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
}
