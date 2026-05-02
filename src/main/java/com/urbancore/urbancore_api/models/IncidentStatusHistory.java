package com.urbancore.urbancore_api.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "incident_status_history")
public class IncidentStatusHistory {

    @Id
    @Column(columnDefinition = "TEXT")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus toStatus;

    @Column(columnDefinition = "TEXT")
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private Instant changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IncidentStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(IncidentStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public IncidentStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(IncidentStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }
}
