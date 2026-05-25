package com.urbancore.urbancore_api.incident.repository.projection;

public interface DailyIncidentCountProjection {
    Object getDateBucket();
    long getCount();
}
