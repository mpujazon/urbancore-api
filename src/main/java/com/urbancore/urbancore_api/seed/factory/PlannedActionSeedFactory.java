package com.urbancore.urbancore_api.seed.factory;

import com.urbancore.urbancore_api.models.PlannedActionStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class PlannedActionSeedFactory {

    public List<PlannedActionSeed> createPlannedActions(Instant anchor) {
        return List.of(
                new PlannedActionSeed(
                        "pa-001",
                        "inc-003",
                        "Validate lighting repair scope",
                        "Electrical contractor will inspect the reported dark corridor and confirm replacement materials.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.plus(2, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.plus(2, ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-002",
                        "inc-004",
                        "Increase cleanup frequency and deep clean zone",
                        "Temporary extra collection rounds and deep cleaning around the playground for one week.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.minus(16, ChronoUnit.DAYS).plus(7, ChronoUnit.HOURS),
                        anchor.minus(16, ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-003",
                        "inc-005",
                        "Replace damaged bench slats",
                        "Parks maintenance team will replace broken slats and secure all fittings.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(21, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES),
                        anchor.minus(21, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-004",
                        "inc-006",
                        "Patch pothole and repaint lane marking",
                        "Road crew will cut the damaged asphalt, apply hot mix, and refresh the affected lane edge.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.minus(12, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS),
                        anchor.minus(12, ChronoUnit.DAYS).plus(10, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-005",
                        "inc-007",
                        "Replace failed streetlight driver",
                        "Maintenance team will replace the driver module and test illumination levels after sunset.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.minus(8, ChronoUnit.DAYS).plus(17, ChronoUnit.HOURS),
                        anchor.minus(8, ChronoUnit.DAYS).plus(20, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-006",
                        "inc-008",
                        "Remove damaged street furniture",
                        "Field team will remove unsafe fixtures and install temporary barriers until replacement stock arrives.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.minus(17, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        anchor.minus(17, ChronoUnit.DAYS).plus(13, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-007",
                        "inc-010",
                        "Night noise monitoring visit",
                        "Civic officers will perform an evening visit and collect evidence for enforcement follow-up.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.plus(1, ChronoUnit.DAYS).plus(20, ChronoUnit.HOURS),
                        anchor.plus(1, ChronoUnit.DAYS).plus(22, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-008",
                        "inc-011",
                        "Graffiti removal and wall treatment",
                        "Cleaning contractor will remove tags and apply protective coating on the affected wall.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.minus(5, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.minus(5, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-009",
                        "inc-013",
                        "Install temporary safety signage",
                        "Mobility team will place temporary signage and cones while the permanent repair order is processed.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(22, ChronoUnit.DAYS).plus(10, ChronoUnit.HOURS),
                        anchor.minus(22, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-010",
                        "inc-020",
                        "Clear overflow waste point",
                        "Waste contractor will remove dumped items, wash the area, and inspect container capacity.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.plus(4, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS),
                        anchor.plus(4, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-011",
                        "inc-025",
                        "Coordinate asphalt repair window",
                        "Operations team will reserve a short traffic restriction window and assign the road crew.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(1, ChronoUnit.DAYS).plus(7, ChronoUnit.HOURS),
                        anchor.minus(1, ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-012",
                        "inc-040",
                        "Repair loose pavement around bus stop",
                        "Public works crew will reset loose slabs and check accessibility around the stop platform.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.plus(1, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.plus(1, ChronoUnit.DAYS).plus(14, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-013",
                        "inc-001",
                        "Complete road surface safety repair",
                        "Road crew will finish compacting the patched asphalt and reopen the affected lane after inspection.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(3, ChronoUnit.DAYS).plus(7, ChronoUnit.HOURS),
                        anchor.minus(3, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-014",
                        "inc-009",
                        "Finalize pavement resurfacing record",
                        "Supervisor will document the completed resurfacing work and close the mobility maintenance ticket.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(18, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.minus(18, ChronoUnit.DAYS).plus(13, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-015",
                        "inc-017",
                        "Stabilize damaged crossing edge",
                        "Mobility team will secure the crossing edge and verify the temporary asphalt patch remains level.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.minus(2, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        anchor.minus(2, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-016",
                        "inc-033",
                        "Schedule permanent streetlight replacement",
                        "Electrical team will replace the damaged luminaire and verify lighting levels on the block.",
                        "admin-barcelona",
                        "admin-barcelona",
                        anchor.plus(5, ChronoUnit.DAYS).plus(18, ChronoUnit.HOURS),
                        anchor.plus(5, ChronoUnit.DAYS).plus(21, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-017",
                        "inc-002",
                        "Repair lighting control cabinet",
                        "Electrical contractor will replace the faulty control cabinet component and test the circuit.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.minus(4, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.minus(4, ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-018",
                        "inc-014",
                        "Close completed noise inspection",
                        "Civic officers will attach the final inspection notes and close the resolved noise case.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.minus(15, ChronoUnit.DAYS).plus(20, ChronoUnit.HOURS),
                        anchor.minus(15, ChronoUnit.DAYS).plus(22, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-019",
                        "inc-018",
                        "Document restored lighting service",
                        "Operations team will confirm the light is operational and publish the service restoration note.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.minus(14, ChronoUnit.DAYS).plus(18, ChronoUnit.HOURS),
                        anchor.minus(14, ChronoUnit.DAYS).plus(20, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-020",
                        "inc-030",
                        "Plan night-time noise follow-up",
                        "Civic officers will schedule a late evening patrol and coordinate evidence collection with residents.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.plus(6, ChronoUnit.DAYS).plus(20, ChronoUnit.HOURS),
                        anchor.plus(6, ChronoUnit.DAYS).plus(22, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-021",
                        "inc-042",
                        "Book graffiti removal contractor",
                        "Cleaning contractor will remove the paint and apply a protective surface treatment.",
                        "admin-lhospitalet",
                        "admin-lhospitalet",
                        anchor.plus(7, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.plus(7, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-022",
                        "inc-015",
                        "Prepare street furniture replacement order",
                        "Parks maintenance will reserve replacement parts and schedule the repair crew.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.plus(3, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        anchor.plus(3, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-023",
                        "inc-019",
                        "Archive completed furniture repair",
                        "Supervisor will confirm the repaired fixture is safe and archive the completed maintenance order.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.minus(13, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        anchor.minus(13, ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-024",
                        "inc-027",
                        "Assign cleaning crew for waste hotspot",
                        "Waste services will add a targeted cleanup visit and inspect nearby container capacity.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.plus(4, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS),
                        anchor.plus(4, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                ),
                new PlannedActionSeed(
                        "pa-025",
                        "inc-035",
                        "Confirm graffiti cleanup completion",
                        "Cleaning supervisor will verify the surface treatment and close the vandalism cleanup ticket.",
                        "admin-santa-coloma",
                        "admin-santa-coloma",
                        anchor.minus(9, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.minus(9, ChronoUnit.DAYS).plus(10, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-026",
                        "inc-012",
                        "Secure general maintenance hazard",
                        "Field team will install temporary protection and complete the active repair order.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.minus(6, ChronoUnit.DAYS).plus(10, ChronoUnit.HOURS),
                        anchor.minus(6, ChronoUnit.DAYS).plus(14, ChronoUnit.HOURS),
                        PlannedActionStatus.CONFIRMED
                ),
                new PlannedActionSeed(
                        "pa-027",
                        "inc-024",
                        "Close completed urban maintenance task",
                        "Operations team will attach final photos and close the completed maintenance intervention.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.minus(12, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS),
                        anchor.minus(12, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS),
                        PlannedActionStatus.DONE
                ),
                new PlannedActionSeed(
                        "pa-028",
                        "inc-036",
                        "Schedule follow-up urban repair crew",
                        "Public works team will schedule the final repair visit and confirm required materials.",
                        "admin-terrassa",
                        "admin-terrassa",
                        anchor.plus(8, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS),
                        anchor.plus(8, ChronoUnit.DAYS).plus(13, ChronoUnit.HOURS),
                        PlannedActionStatus.PLANNED
                )
        );
    }

    public record PlannedActionSeed(
            String key,
            String incidentSeedKey,
            String title,
            String description,
            String createdByUserRef,
            String assignedToUserRef,
            Instant scheduledStart,
            Instant scheduledEnd,
            PlannedActionStatus status
    ) {
        public Instant defaultIncidentPlannedAt() {
            return scheduledStart.minus(2, ChronoUnit.DAYS);
        }
    }
}
