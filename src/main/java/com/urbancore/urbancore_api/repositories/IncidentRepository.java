package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, String> {
    List<Incident> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
}
