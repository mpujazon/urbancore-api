package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, String>, JpaSpecificationExecutor<Incident> {
    List<Incident> findAllByOrderByCreatedAtDesc();
    List<Incident> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
}
