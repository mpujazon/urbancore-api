package com.urbancore.urbancore_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "planned_actions")
public class PlannedAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @NotBlank
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull
    @Column(name = "scheduled_start", nullable = false)
    private Instant scheduledStart;

    @Column(name = "scheduled_end")
    private Instant scheduledEnd;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlannedActionStatus status = PlannedActionStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedToUser;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PlannedAction() {
    }

    public PlannedAction(
            Incident incident,
            String title,
            String description,
            Instant scheduledStart,
            Instant scheduledEnd,
            User assignedToUser,
            User createdBy
    ) {
        this.incident = incident;
        this.title = title;
        this.description = description;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.assignedToUser = assignedToUser;
        this.createdBy = createdBy;
        this.status = PlannedActionStatus.PLANNED;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getScheduledStart() {
        return scheduledStart;
    }

    public Instant getScheduledEnd() {
        return scheduledEnd;
    }

    public PlannedActionStatus getStatus() {
        return status;
    }

    public User getAssignedToUser() {
        return assignedToUser;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateDetails(
            String title,
            String description,
            Instant scheduledStart,
            Instant scheduledEnd,
            User assignedToUser
    ) {
        this.title = title;
        this.description = description;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.assignedToUser = assignedToUser;
    }

    public void changeStatus(PlannedActionStatus status) {
        this.status = status;
    }

    public void cancel() {
        this.status = PlannedActionStatus.CANCELLED;
    }
}
