package com.urbancore.urbancore_api.repositories.projections;

public interface DailyIncidentCountProjection {
    Object getDateBucket();
    long getCount();
}
