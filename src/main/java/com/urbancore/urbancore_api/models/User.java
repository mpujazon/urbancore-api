package com.urbancore.urbancore_api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Schema(description = "UrbanCore user profile synchronized from Firebase Auth")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Internal user identifier", example = "42")
    private Long id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Firebase Auth UID", example = "abc123def456")
    private String firebaseUid;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "User email from Firebase", example = "citizen@example.com")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Authorization role", example = "ROLE_CITIZEN")
    private UserRole role;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "City identifier assigned to the user for scoped admin operations", example = "2f3c7a4e-9d2b-4f16-a51d-9d4b2f6e0c12", nullable = true)
    private String cityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
