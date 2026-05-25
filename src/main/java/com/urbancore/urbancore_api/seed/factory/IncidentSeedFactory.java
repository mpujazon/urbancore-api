package com.urbancore.urbancore_api.seed.factory;

import com.urbancore.urbancore_api.incident.entity.IncidentCategory;
import com.urbancore.urbancore_api.incident.entity.IncidentPriority;
import com.urbancore.urbancore_api.incident.entity.IncidentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class IncidentSeedFactory {

    public List<IncidentSeed> createIncidents(Random random, Instant anchor) {
        List<IncidentSeed> seeds = new ArrayList<>();

        CitySpec[] cities = new CitySpec[] {
                new CitySpec("barcelona", List.of(
                        new AddressLocation("Eixample", "Carrer de Mallorca 401, Eixample", 41.4041, 2.1744),
                        new AddressLocation("Sants", "Carrer de Sants 79, Sants", 41.3753, 2.1337),
                        new AddressLocation("Gracia", "Travessera de Gracia 181, Gracia", 41.4028, 2.1566),
                        new AddressLocation("Poblenou", "Rambla del Poblenou 102, Poblenou", 41.4016, 2.2036),
                        new AddressLocation("Eixample", "Carrer d'Arago 275, Eixample", 41.3922, 2.1644),
                        new AddressLocation("Sants", "Carrer de Creu Coberta 95, Sants", 41.3757, 2.1442),
                        new AddressLocation("Gracia", "Carrer de Verdi 32, Gracia", 41.4047, 2.1572),
                        new AddressLocation("Poblenou", "Carrer de Pere IV 196, Poblenou", 41.4074, 2.1992),
                        new AddressLocation("Eixample", "Avinguda Diagonal 459, Eixample", 41.3939, 2.1570),
                        new AddressLocation("Sants", "Passeig de Sant Antoni 36, Sants", 41.3792, 2.1394),
                        new AddressLocation("Gracia", "Carrer del Torrent de l'Olla 86, Gracia", 41.4022, 2.1561),
                        new AddressLocation("Poblenou", "Carrer de Bilbao 101, Poblenou", 41.4052, 2.2008),
                        new AddressLocation("Eixample", "Carrer de Valencia 352, Eixample", 41.3963, 2.1695),
                        new AddressLocation("Sants", "Carrer de Tarragona 129, Sants", 41.3806, 2.1453),
                        new AddressLocation("Gracia", "Placa de la Virreina 1, Gracia", 41.4058, 2.1576),
                        new AddressLocation("Poblenou", "Carrer de Llull 230, Poblenou", 41.4043, 2.2028),
                        new AddressLocation("Eixample", "Carrer del Consell de Cent 365, Eixample", 41.3934, 2.1678),
                        new AddressLocation("Sants", "Rambla de Badal 121, Sants", 41.3687, 2.1317)
                ), List.of("citizen-lucas", "citizen-sofia")),
                new CitySpec("lhospitalet", List.of(
                        new AddressLocation("Collblanc", "Carrer Progres 54, Collblanc", 41.3761, 2.1194),
                        new AddressLocation("Bellvitge", "Rambla de la Marina 265, Bellvitge", 41.3488, 2.1087),
                        new AddressLocation("Santa Eulalia", "Carrer de Santa Eulalia 126, Santa Eulalia", 41.3659, 2.1284),
                        new AddressLocation("Centre", "Rambla Just Oliveras 48, Centre", 41.3607, 2.1008),
                        new AddressLocation("Collblanc", "Carretera de Collblanc 72, Collblanc", 41.3755, 2.1163),
                        new AddressLocation("Bellvitge", "Avinguda Mare de Deu de Bellvitge 18, Bellvitge", 41.3505, 2.1112),
                        new AddressLocation("Santa Eulalia", "Avinguda del Carrilet 209, Santa Eulalia", 41.3627, 2.1241),
                        new AddressLocation("Centre", "Carrer Major 31, Centre", 41.3601, 2.0990),
                        new AddressLocation("Collblanc", "Carrer de la Riera Blanca 155, Collblanc", 41.3728, 2.1242),
                        new AddressLocation("Bellvitge", "Travessia Industrial 157, Bellvitge", 41.3529, 2.1056),
                        new AddressLocation("Santa Eulalia", "Carrer Amadeu Torner 74, Santa Eulalia", 41.3640, 2.1311),
                        new AddressLocation("Centre", "Carrer de Barcelona 72, Centre", 41.3592, 2.0986),
                        new AddressLocation("Collblanc", "Carrer del Llobregat 91, Collblanc", 41.3745, 2.1182),
                        new AddressLocation("Bellvitge", "Carrer de l'Ermita de Bellvitge 40, Bellvitge", 41.3484, 2.1124),
                        new AddressLocation("Santa Eulalia", "Carrer Jacint Verdaguer 45, Santa Eulalia", 41.3668, 2.1266),
                        new AddressLocation("Centre", "Avinguda Josep Tarradellas i Joan 44, Centre", 41.3618, 2.0969),
                        new AddressLocation("Collblanc", "Carrer Occident 22, Collblanc", 41.3768, 2.1210),
                        new AddressLocation("Bellvitge", "Carrer Portugal 9, Bellvitge", 41.3516, 2.1095)
                ), List.of("citizen-emma", "citizen-daniel")),
                new CitySpec("santa-coloma", List.of(
                        new AddressLocation("Fondo", "Carrer del Rellotge 38, Fondo", 41.4495, 2.2192),
                        new AddressLocation("Riu Nord", "Passeig de la Salzereda 52, Riu Nord", 41.4551, 2.2099),
                        new AddressLocation("Can Mariner", "Carrer de Sant Carles 62, Can Mariner", 41.4526, 2.2141),
                        new AddressLocation("Centre", "Avinguda de la Generalitat 22, Centre", 41.4519, 2.2084),
                        new AddressLocation("Fondo", "Carrer de Mozart 19, Fondo", 41.4483, 2.2180),
                        new AddressLocation("Riu Nord", "Carrer de Pompeu Fabra 41, Riu Nord", 41.4570, 2.2118),
                        new AddressLocation("Can Mariner", "Carrer de Sant Jeroni 47, Can Mariner", 41.4536, 2.2132),
                        new AddressLocation("Centre", "Carrer Major 34, Centre", 41.4515, 2.2071),
                        new AddressLocation("Fondo", "Carrer de Beethoven 12, Fondo", 41.4478, 2.2203),
                        new AddressLocation("Riu Nord", "Avinguda de Santa Coloma 71, Riu Nord", 41.4560, 2.2078),
                        new AddressLocation("Can Mariner", "Carrer de Rafael Casanova 81, Can Mariner", 41.4547, 2.2150),
                        new AddressLocation("Centre", "Rambla de Sant Sebastia 49, Centre", 41.4504, 2.2090),
                        new AddressLocation("Fondo", "Carrer de Wagner 44, Fondo", 41.4490, 2.2214),
                        new AddressLocation("Riu Nord", "Carrer de Sant Joaquim 96, Riu Nord", 41.4568, 2.2104),
                        new AddressLocation("Can Mariner", "Carrer de Milans 28, Can Mariner", 41.4530, 2.2162),
                        new AddressLocation("Centre", "Placa de la Vila 1, Centre", 41.4511, 2.2080),
                        new AddressLocation("Fondo", "Carrer de Sicilia 63, Fondo", 41.4487, 2.2170),
                        new AddressLocation("Riu Nord", "Carrer de Francesc Moragas 77, Riu Nord", 41.4557, 2.2126)
                ), List.of("citizen-noah", "citizen-laia")),
                new CitySpec("terrassa", List.of(
                        new AddressLocation("Sant Pere", "Carrer Major de Sant Pere 77, Sant Pere", 41.5685, 2.0163),
                        new AddressLocation("Centre", "Rambla d'Egara 269, Centre", 41.5624, 2.0100),
                        new AddressLocation("Can Boada", "Avinguda de Can Boada 32, Can Boada", 41.5680, 1.9947),
                        new AddressLocation("Egara", "Avinguda de Barcelona 184, Egara", 41.5658, 2.0255),
                        new AddressLocation("Sant Pere", "Carrer de la Creu Gran 15, Sant Pere", 41.5667, 2.0140),
                        new AddressLocation("Centre", "Carrer de la Rasa 24, Centre", 41.5631, 2.0091),
                        new AddressLocation("Can Boada", "Carrer d'Alexandre Gali 91, Can Boada", 41.5705, 1.9981),
                        new AddressLocation("Egara", "Carrer d'Egara 39, Egara", 41.5646, 2.0202),
                        new AddressLocation("Sant Pere", "Carrer Ample 111, Sant Pere", 41.5696, 2.0131),
                        new AddressLocation("Centre", "Carrer de la Font Vella 57, Centre", 41.5639, 2.0114),
                        new AddressLocation("Can Boada", "Carrer de Josep Trueta 7, Can Boada", 41.5672, 1.9919),
                        new AddressLocation("Egara", "Carretera de Castellar 117, Egara", 41.5704, 2.0237),
                        new AddressLocation("Sant Pere", "Carrer de Bartomeu Amat 42, Sant Pere", 41.5677, 2.0181),
                        new AddressLocation("Centre", "Passeig del Comte d'Egara 8, Centre", 41.5628, 2.0130),
                        new AddressLocation("Can Boada", "Carrer de Maria Auxiliadora 176, Can Boada", 41.5657, 1.9973),
                        new AddressLocation("Egara", "Carrer de Jacint Elias 71, Egara", 41.5669, 2.0220),
                        new AddressLocation("Sant Pere", "Carrer de la Independencia 92, Sant Pere", 41.5714, 2.0155),
                        new AddressLocation("Centre", "Carrer de Gutenberg 3, Centre", 41.5606, 2.0078)
                ), List.of("citizen-marc", "citizen-ines"))
        };

        IncidentCategory[] categories = IncidentCategory.values();
        int totalIncidents = 72;
        for (int i = 1; i <= totalIncidents; i++) {
            String key = String.format("inc-%03d", i);
            CitySpec citySpec = cities[(i - 1) % cities.length];
            IncidentCategory category = categories[(i - 1) % categories.length];
            int daysAgo = 30 - ((i - 1) * 30 / totalIncidents);
            IncidentStatus finalStatus = pickFinalStatus(i, daysAgo);
            IncidentPriority priority = pickPriority(i, finalStatus);

            int cityIncidentIndex = (i - 1) / cities.length;
            String reporterRef = citySpec.reporters().get(cityIncidentIndex % citySpec.reporters().size());
            AddressLocation location = citySpec.locations().get(cityIncidentIndex % citySpec.locations().size());
            String neighborhood = location.neighborhood();

            String title = buildTitle(category, neighborhood);
            String description = buildDescription(category, neighborhood, finalStatus);

            Instant createdAt = anchor.minus(daysAgo, ChronoUnit.DAYS).plus(random.nextInt(600), ChronoUnit.MINUTES);
            List<IncidentStatus> chain = buildStatusChain(finalStatus);
            List<Instant> statusChangedAt = buildStatusChangedAt(chain, createdAt, anchor);
            Instant updatedAt = statusChangedAt.get(statusChangedAt.size() - 1).plus(random.nextInt(240), ChronoUnit.MINUTES);

            seeds.add(new IncidentSeed(
                    key,
                    title,
                    description,
                    category,
                    finalStatus,
                    priority,
                    citySpec.cityRef(),
                    reporterRef,
                    location.lat(),
                    location.lng(),
                    location.addressLabel(),
                    createdAt,
                    updatedAt,
                    chain,
                    statusChangedAt
            ));
        }

        // Keep deterministic references used by planned action factory coherent.
        forceStatus(seeds, "inc-003", IncidentStatus.PLANNED, anchor);
        forceStatus(seeds, "inc-004", IncidentStatus.IN_PROGRESS, anchor);
        forceStatus(seeds, "inc-005", IncidentStatus.RESOLVED, anchor);

        return seeds;
    }

    private void forceStatus(List<IncidentSeed> seeds, String key, IncidentStatus status, Instant anchor) {
        for (int i = 0; i < seeds.size(); i++) {
            IncidentSeed current = seeds.get(i);
            if (!current.key().equals(key)) {
                continue;
            }
            List<IncidentStatus> chain = buildStatusChain(status);
            List<Instant> statusChangedAt = buildStatusChangedAt(chain, current.createdAt(), anchor);
            seeds.set(i, new IncidentSeed(
                    current.key(),
                    current.title(),
                    current.description(),
                    current.category(),
                    status,
                    current.priority(),
                    current.cityRef(),
                    current.reporterRef(),
                    current.lat(),
                    current.lng(),
                    current.addressLabel(),
                    current.createdAt(),
                    statusChangedAt.get(statusChangedAt.size() - 1).plus(2, ChronoUnit.HOURS),
                    chain,
                    statusChangedAt
            ));
            return;
        }
    }

    private IncidentStatus pickFinalStatus(int index, int daysAgo) {
        if (daysAgo <= 2) {
            return IncidentStatus.NEW;
        }
        if (daysAgo <= 6) {
            return index % 3 == 0 ? IncidentStatus.NEW : IncidentStatus.UNDER_REVIEW;
        }
        if (daysAgo <= 12) {
            return index % 3 == 0 ? IncidentStatus.UNDER_REVIEW : IncidentStatus.PLANNED;
        }
        if (daysAgo <= 20) {
            return switch (index % 3) {
                case 0 -> IncidentStatus.PLANNED;
                case 1 -> IncidentStatus.IN_PROGRESS;
                default -> IncidentStatus.RESOLVED;
            };
        }
        return switch (index % 5) {
            case 0 -> IncidentStatus.PLANNED;
            case 1, 2 -> IncidentStatus.IN_PROGRESS;
            default -> IncidentStatus.RESOLVED;
        };
    }

    private IncidentPriority pickPriority(int index, IncidentStatus finalStatus) {
        if (finalStatus == IncidentStatus.RESOLVED || finalStatus == IncidentStatus.IN_PROGRESS) {
            return index % 3 == 0 ? IncidentPriority.HIGH : IncidentPriority.MEDIUM;
        }
        if (finalStatus == IncidentStatus.PLANNED) {
            return index % 4 == 0 ? IncidentPriority.HIGH : IncidentPriority.MEDIUM;
        }
        return index % 5 == 0 ? IncidentPriority.HIGH : IncidentPriority.LOW;
    }

    private String buildTitle(IncidentCategory category, String neighborhood) {
        return switch (category) {
            case POTHOLE -> "Road surface damage reported in " + neighborhood;
            case LIGHTING -> "Public lighting failure affecting " + neighborhood;
            case STREET_FURNITURE -> "Damaged street furniture in " + neighborhood;
            case CLEANLINESS -> "Cleanliness issue detected in " + neighborhood;
            case NOISE -> "Recurring noise complaint in " + neighborhood;
            case GRAFFITI -> "Graffiti vandalism reported in " + neighborhood;
            case OTHER -> "General urban maintenance issue in " + neighborhood;
        };
    }

    private String buildDescription(IncidentCategory category, String neighborhood, IncidentStatus finalStatus) {
        String statusClause = switch (finalStatus) {
            case NEW -> "The report was recently submitted and awaits triage.";
            case UNDER_REVIEW -> "City staff reviewed initial evidence and requested technical validation.";
            case PLANNED -> "The intervention was approved and scheduled with municipal teams.";
            case IN_PROGRESS -> "Field operations are currently active and monitored by city staff.";
            case RESOLVED -> "The issue was completed and closed after on-site verification.";
            default -> "The incident is part of the production demo lifecycle.";
        };

        String categoryClause = switch (category) {
            case POTHOLE -> "Residents reported uneven asphalt that may affect bicycles, scooters, and buses.";
            case LIGHTING -> "A failed lamp leaves a dark corridor and reduces nighttime pedestrian safety.";
            case STREET_FURNITURE -> "A public asset is damaged and requires repair to avoid safety risks.";
            case CLEANLINESS -> "Waste overflow and scattered litter are affecting hygiene and accessibility.";
            case NOISE -> "Repeated high-volume activity during late hours is disturbing nearby homes.";
            case GRAFFITI -> "Unauthorized paint on public surfaces affects visibility and neighborhood upkeep.";
            case OTHER -> "The report concerns a non-categorized urban maintenance condition.";
        };

        return categoryClause + " Location context: " + neighborhood + ". " + statusClause;
    }

    private List<IncidentStatus> buildStatusChain(IncidentStatus finalStatus) {
        List<IncidentStatus> chain = new ArrayList<>();
        chain.add(IncidentStatus.NEW);
        if (finalStatus == IncidentStatus.NEW) {
            return chain;
        }
        chain.add(IncidentStatus.UNDER_REVIEW);
        if (finalStatus == IncidentStatus.UNDER_REVIEW) {
            return chain;
        }
        chain.add(IncidentStatus.PLANNED);
        if (finalStatus == IncidentStatus.PLANNED) {
            return chain;
        }
        chain.add(IncidentStatus.IN_PROGRESS);
        if (finalStatus == IncidentStatus.IN_PROGRESS) {
            return chain;
        }
        chain.add(IncidentStatus.RESOLVED);
        return chain;
    }

    private List<Instant> buildStatusChangedAt(List<IncidentStatus> chain, Instant createdAt, Instant anchor) {
        List<Instant> changedAt = new ArrayList<>();
        changedAt.add(createdAt);
        if (chain.size() == 1) {
            return changedAt;
        }

        long availableHours = Math.max(chain.size() * 12L, ChronoUnit.HOURS.between(createdAt, anchor));
        long spacingHours = Math.max(12L, availableHours / (chain.size() + 1));
        for (int i = 1; i < chain.size(); i++) {
            Instant transitionAt = createdAt
                    .plus(spacingHours * i, ChronoUnit.HOURS)
                    .plus((long) i * 37L, ChronoUnit.MINUTES);
            Instant latestAllowed = anchor.minus(chain.size() - i, ChronoUnit.HOURS);
            changedAt.add(transitionAt.isAfter(latestAllowed) ? latestAllowed : transitionAt);
        }
        return changedAt;
    }

    private record CitySpec(
            String cityRef,
            List<AddressLocation> locations,
            List<String> reporters
    ) {
    }

    private record AddressLocation(String neighborhood, String addressLabel, double lat, double lng) {
    }

    public record IncidentSeed(
            String key,
            String title,
            String description,
            IncidentCategory category,
            IncidentStatus finalStatus,
            IncidentPriority priority,
            String cityRef,
            String reporterRef,
            double lat,
            double lng,
            String addressLabel,
            Instant createdAt,
            Instant updatedAt,
            List<IncidentStatus> statusChain,
            List<Instant> statusChangedAt
    ) {
    }
}
