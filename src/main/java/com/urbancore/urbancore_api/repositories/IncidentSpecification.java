package com.urbancore.urbancore_api.repositories;

import com.urbancore.urbancore_api.dtos.IncidentFilterDto;
import com.urbancore.urbancore_api.models.Incident;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class IncidentSpecification {

    public static Specification<Incident> withFilters(IncidentFilterDto filters) {
        Specification<Incident> spec = Specification.where(null);

        if (filters.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), filters.status()));
        }
        if (filters.category() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), filters.category()));
        }
        if (filters.priority() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), filters.priority()));
        }
        if (filters.cityId() != null && !filters.cityId().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cityId"), filters.cityId()));
        }
        if (filters.from() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), filters.from()));
        }
        if (filters.to() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), filters.to()));
        }
        if (filters.q() != null && !filters.q().isBlank()) {
            String pattern = "%" + filters.q().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                return cb.or(titleLike, descLike);
            });
        }

        return spec;
    }
}
