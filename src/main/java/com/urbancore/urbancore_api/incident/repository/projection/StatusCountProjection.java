package com.urbancore.urbancore_api.incident.repository.projection;

import com.urbancore.urbancore_api.incident.entity.IncidentStatus;

public interface StatusCountProjection {
    IncidentStatus getStatus();
    long getCount();
}
