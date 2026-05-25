package com.urbancore.urbancore_api.seed;

import com.urbancore.urbancore_api.city.entity.City;
import com.urbancore.urbancore_api.incident.entity.Incident;
import com.urbancore.urbancore_api.incident.entity.IncidentImage;
import com.urbancore.urbancore_api.incident.entity.IncidentPriority;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import com.urbancore.urbancore_api.incident.entity.IncidentStatusHistory;
import com.urbancore.urbancore_api.plannedaction.entity.PlannedAction;
import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.city.repository.CityRepository;
import com.urbancore.urbancore_api.incident.repository.IncidentRepository;
import com.urbancore.urbancore_api.plannedaction.repository.PlannedActionRepository;
import com.urbancore.urbancore_api.auth.repository.UserRepository;
import com.urbancore.urbancore_api.seed.factory.CitySeedFactory;
import com.urbancore.urbancore_api.seed.factory.IncidentSeedFactory;
import com.urbancore.urbancore_api.seed.factory.PlannedActionSeedFactory;
import com.urbancore.urbancore_api.seed.factory.UserSeedFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Component
public class ProductionDemoSeeder implements DevDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(ProductionDemoSeeder.class);
    private static final long RANDOM_SEED = 20260525L;

    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;
    private final PlannedActionRepository plannedActionRepository;
    private final SeedRegistryService seedRegistryService;
    private final SeedImageCatalog seedImageCatalog;
    private final CitySeedFactory cityFactory;
    private final UserSeedFactory userFactory;
    private final IncidentSeedFactory incidentFactory;
    private final PlannedActionSeedFactory plannedActionFactory;

    public ProductionDemoSeeder(
            CityRepository cityRepository,
            UserRepository userRepository,
            IncidentRepository incidentRepository,
            PlannedActionRepository plannedActionRepository,
            SeedRegistryService seedRegistryService,
            SeedImageCatalog seedImageCatalog,
            CitySeedFactory cityFactory,
            UserSeedFactory userFactory,
            IncidentSeedFactory incidentFactory,
            PlannedActionSeedFactory plannedActionFactory
    ) {
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.incidentRepository = incidentRepository;
        this.plannedActionRepository = plannedActionRepository;
        this.seedRegistryService = seedRegistryService;
        this.seedImageCatalog = seedImageCatalog;
        this.cityFactory = cityFactory;
        this.userFactory = userFactory;
        this.incidentFactory = incidentFactory;
        this.plannedActionFactory = plannedActionFactory;
    }

    @Override
    @Transactional
    public void seed() {
        Random random = new Random(RANDOM_SEED);
        Instant anchorTime = Instant.parse("2026-05-01T09:00:00Z");

        Map<String, City> cityByRef = new HashMap<>();
        Map<String, User> userByRef = new HashMap<>();
        Map<String, Incident> incidentByRef = new HashMap<>();

        Counter cityCounter = new Counter();
        Counter userCounter = new Counter();
        Counter incidentCounter = new Counter();
        Counter plannedActionCounter = new Counter();

        for (CitySeedFactory.CitySeed citySeed : cityFactory.createCities()) {
            String seedKey = "production-demo:city:" + citySeed.key();
            Optional<City> existing = cityRepository.findBySlug(citySeed.slug());
            if (seedRegistryService.isSeeded(seedKey) || existing.isPresent()) {
                City city = existing.orElseGet(() -> cityRepository.findBySlug(citySeed.slug()).orElseThrow());
                cityByRef.put(citySeed.key(), city);
                cityCounter.skipped++;
                continue;
            }

            City city = new City();
            city.setId(UUID.nameUUIDFromBytes(seedKey.getBytes(StandardCharsets.UTF_8)));
            city.setName(citySeed.name());
            city.setSlug(citySeed.slug());
            City saved = cityRepository.save(city);
            seedRegistryService.register(seedKey, "CITY", saved.getId().toString());
            cityByRef.put(citySeed.key(), saved);
            cityCounter.created++;
        }

        for (UserSeedFactory.UserSeed userSeed : userFactory.createUsers()) {
            String seedKey = "production-demo:user:" + userSeed.key();
            Optional<User> existing = userRepository.findByFirebaseUid(userSeed.firebaseUid());
            if (seedRegistryService.isSeeded(seedKey) || existing.isPresent()) {
                User user = existing.orElseGet(() -> userRepository.findByFirebaseUid(userSeed.firebaseUid()).orElseThrow());
                userByRef.put(userSeed.key(), user);
                userCounter.skipped++;
                continue;
            }

            City city = userSeed.cityRef() == null ? null : cityByRef.get(userSeed.cityRef());
            if (userSeed.cityRef() != null && city == null) {
                throw new IllegalStateException("Missing city reference for user seed: " + userSeed.key());
            }

            User user = new User();
            user.setFirebaseUid(userSeed.firebaseUid());
            user.setEmail(userSeed.email());
            user.setRole(userSeed.role());
            user.setCityId(city == null ? null : city.getId().toString());
            User saved = userRepository.save(user);
            seedRegistryService.register(seedKey, "USER", String.valueOf(saved.getId()));
            userByRef.put(userSeed.key(), saved);
            userCounter.created++;
        }

        for (IncidentSeedFactory.IncidentSeed incidentSeed : incidentFactory.createIncidents(random, anchorTime)) {
            String seedKey = "production-demo:incident:" + incidentSeed.key();
            String incidentId = deterministicStringId(seedKey);
            Optional<Incident> existing = incidentRepository.findById(incidentId);
            if (seedRegistryService.isSeeded(seedKey) || existing.isPresent()) {
                Incident incident = existing.orElseGet(() -> incidentRepository.findById(incidentId).orElseThrow());
                incidentByRef.put(incidentSeed.key(), incident);
                incidentCounter.skipped++;
                continue;
            }

            City city = cityByRef.get(incidentSeed.cityRef());
            User reporter = userByRef.get(incidentSeed.reporterRef());
            if (city == null || reporter == null) {
                throw new IllegalStateException("Missing city or reporter for incident seed: " + incidentSeed.key());
            }

            Incident incident = new Incident();
            incident.setId(incidentId);
            incident.setTitle(incidentSeed.title());
            incident.setDescription(incidentSeed.description());
            incident.setCategory(incidentSeed.category());
            incident.setStatus(incidentSeed.finalStatus());
            incident.setPriority(incidentSeed.priority() == null ? IncidentPriority.UNDEFINED : incidentSeed.priority());
            incident.setCityId(city.getId().toString());
            incident.setReporter(reporter);
            incident.setLat(incidentSeed.lat());
            incident.setLng(incidentSeed.lng());
            incident.setAddressLabel(incidentSeed.addressLabel());
            incident.setCity(city.getName());
            incident.setGeohash(incidentSeed.lat() + "," + incidentSeed.lng());
            incident.setCreatedAt(incidentSeed.createdAt());
            incident.setUpdatedAt(incidentSeed.updatedAt());

            SeedImageCatalog.SeedImageAsset imageAsset = seedImageCatalog.pickImage(incidentSeed.category(), random);
            IncidentImage image = new IncidentImage();
            image.setId(deterministicStringId(seedKey + ":img:0"));
            image.setUrl(imageAsset.url());
            image.setThumbnailUrl(imageAsset.thumbnailUrl());
            image.setPublicId(imageAsset.publicId());
            image.setMimeType("image/jpeg");
            image.setSizeKb(420 + random.nextInt(900));
            image.setIncident(incident);
            incident.setImages(new ArrayList<>(List.of(image)));

            List<IncidentStatusHistory> history = new ArrayList<>();
            IncidentStatus previous = null;
            for (int i = 0; i < incidentSeed.statusChain().size(); i++) {
                IncidentStatus toStatus = incidentSeed.statusChain().get(i);
                IncidentStatusHistory entry = new IncidentStatusHistory();
                entry.setId(deterministicStringId(seedKey + ":history:" + i));
                entry.setFromStatus(previous);
                entry.setToStatus(toStatus);
                entry.setChangedBy(String.valueOf(reporter.getId()));
                entry.setReason(i == 0 ? "Initial production demo state" : "Operational progression in demo lifecycle");
                entry.setChangedAt(incidentSeed.statusChangedAt().get(i));
                entry.setIncident(incident);
                history.add(entry);
                previous = toStatus;
            }
            incident.setStatusHistory(history);

            Incident saved = incidentRepository.save(incident);
            seedRegistryService.register(seedKey, "INCIDENT", saved.getId());
            incidentByRef.put(incidentSeed.key(), saved);
            incidentCounter.created++;
        }

        for (PlannedActionSeedFactory.PlannedActionSeed actionSeed : plannedActionFactory.createPlannedActions(anchorTime)) {
            String seedKey = "production-demo:planned-action:" + actionSeed.key();
            Incident incident = incidentByRef.get(actionSeed.incidentSeedKey());
            if (incident == null) {
                throw new IllegalStateException("Missing incident reference for planned action seed: " + actionSeed.key());
            }
            User createdBy = userByRef.get(actionSeed.createdByUserRef());
            User assignedTo = userByRef.get(actionSeed.assignedToUserRef());
            if (createdBy == null) {
                throw new IllegalStateException("Missing createdBy user for planned action seed: " + actionSeed.key());
            }

            if (seedRegistryService.isSeeded(seedKey) || plannedActionRepository.existsByIncidentIdAndTitle(incident.getId(), actionSeed.title())) {
                plannedActionCounter.skipped++;
                continue;
            }

            if (incident.getStatus() == IncidentStatus.NEW || incident.getStatus() == IncidentStatus.UNDER_REVIEW) {
                IncidentStatus fromStatus = incident.getStatus();
                incident.setStatus(IncidentStatus.PLANNED);
                IncidentStatusHistory historyEntry = new IncidentStatusHistory();
                historyEntry.setId(deterministicStringId(seedKey + ":status-planned"));
                historyEntry.setFromStatus(fromStatus);
                historyEntry.setToStatus(IncidentStatus.PLANNED);
                historyEntry.setChangedBy(String.valueOf(createdBy.getId()));
                historyEntry.setReason("Planned action created in production demo seed");
                historyEntry.setChangedAt(actionSeed.defaultIncidentPlannedAt());
                historyEntry.setIncident(incident);
                incident.getStatusHistory().add(historyEntry);
                incidentRepository.save(incident);
            }

            PlannedAction plannedAction = new PlannedAction(
                    incident,
                    actionSeed.title(),
                    actionSeed.description(),
                    actionSeed.scheduledStart(),
                    actionSeed.scheduledEnd(),
                    assignedTo,
                    createdBy
            );
            plannedAction.changeStatus(actionSeed.status());
            PlannedAction saved = plannedActionRepository.save(plannedAction);
            seedRegistryService.register(seedKey, "PLANNED_ACTION", String.valueOf(saved.getId()));
            plannedActionCounter.created++;
        }

        log.info("Seed mode: production-demo");
        log.info("Cities created/skipped: {}/{}", cityCounter.created, cityCounter.skipped);
        log.info("Users created/skipped: {}/{}", userCounter.created, userCounter.skipped);
        log.info("Incidents created/skipped: {}/{}", incidentCounter.created, incidentCounter.skipped);
        log.info("Planned actions created/skipped: {}/{}", plannedActionCounter.created, plannedActionCounter.skipped);
    }

    private String deterministicStringId(String seedKey) {
        return UUID.nameUUIDFromBytes(seedKey.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static final class Counter {
        int created;
        int skipped;
    }
}
