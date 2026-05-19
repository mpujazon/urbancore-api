package com.urbancore.urbancore_api.repositories.projections;

public interface IncidentPlannedActionsCountProjection {
    String getIncidentId();
    long getPlannedActionsCount();
}
