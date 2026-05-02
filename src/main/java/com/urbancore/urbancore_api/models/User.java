package com.urbancore.urbancore_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String firebaseUid;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
