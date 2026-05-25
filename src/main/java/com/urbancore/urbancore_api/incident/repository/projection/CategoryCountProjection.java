package com.urbancore.urbancore_api.incident.repository.projection;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;

public interface CategoryCountProjection {
    IncidentCategory getCategory();
    long getCount();
}
