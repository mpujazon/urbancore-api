package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, String> {
}
