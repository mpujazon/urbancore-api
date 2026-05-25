package com.urbancore.urbancore_api.incident.repository.projection;

public interface IncidentPlannedActionsCountProjection {
    String getIncidentId();
    long getPlannedActionsCount();
}
