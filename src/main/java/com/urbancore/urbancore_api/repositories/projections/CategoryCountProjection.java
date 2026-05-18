package com.urbancore.urbancore_api.repositories.projections;

import com.urbancore.urbancore_api.models.IncidentCategory;

public interface CategoryCountProjection {
    IncidentCategory getCategory();
    long getCount();
}
