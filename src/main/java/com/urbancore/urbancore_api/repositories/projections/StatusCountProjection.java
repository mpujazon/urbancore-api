package com.urbancore.urbancore_api.repositories.projections;

import com.urbancore.urbancore_api.models.IncidentStatus;

public interface StatusCountProjection {
    IncidentStatus getStatus();
    long getCount();
}
