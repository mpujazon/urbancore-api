package com.urbancore.urbancore_api.controllers;

import com.urbancore.urbancore_api.models.*;
import com.urbancore.urbancore_api.repositories.IncidentRepository;
import com.urbancore.urbancore_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        incidentRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("citizen@example.com");
        testUser.setFirebaseUid("test-firebase-uid-" + UUID.randomUUID());
        testUser.setRole(UserRole.ROLE_CITIZEN);
        testUser = userRepository.save(testUser);
    }

    private Incident createIncident(String title, IncidentCategory category, IncidentStatus status,
                                     IncidentPriority priority, String cityId) {
        Incident incident = new Incident();
        incident.setTitle(title);
        incident.setDescription("Description for " + title);
        incident.setCategory(category);
        incident.setStatus(status);
        incident.setPriority(priority);
        incident.setCityId(cityId);
        incident.setReporter(testUser);
        incident.setLat(41.3874);
        incident.setLng(2.1686);
        incident.setAddressLabel("Test Address");
        incident.setCity("Test City");
        incident.setGeohash("sp3e3w");
        incident.setImages(List.of());
        incident.setStatusHistory(List.of());
        incident.onCreate();
        return incidentRepository.save(incident);
    }

    @Nested
    @DisplayName("Public incident detail")
    class PublicIncidentDetail {

        @Test
        @DisplayName("should return public-safe incident detail by id")
        void returnsPublicDetail() throws Exception {
            Incident incident = createIncident("Detailed Incident", IncidentCategory.LIGHTING, IncidentStatus.UNDER_REVIEW,
                    IncidentPriority.HIGH, "city_bcn");

            IncidentImage image = new IncidentImage();
            image.setId("img-001");
            image.setUrl("https://example.com/image.jpg");
            image.setThumbnailUrl("https://example.com/thumb.jpg");
            image.setPublicId("private-cloudinary-id");
            image.setMimeType("image/jpeg");
            image.setSizeKb(120);
            image.setIncident(incident);

            IncidentStatusHistory history = new IncidentStatusHistory();
            history.setId("hist-001");
            history.setFromStatus(IncidentStatus.NEW);
            history.setToStatus(IncidentStatus.UNDER_REVIEW);
            history.setChangedBy("admin-123");
            history.setReason("internal note");
            history.setChangedAt(Instant.now());
            history.setIncident(incident);

            incident.setImages(List.of(image));
            incident.setStatusHistory(List.of(history));
            incident = incidentRepository.save(incident);

            mockMvc.perform(get("/api/incidents/{id}", incident.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(incident.getId()))
                    .andExpect(jsonPath("$.title").value("Detailed Incident"))
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.category").value("LIGHTING"))
                    .andExpect(jsonPath("$.status").value("UNDER_REVIEW"))
                    .andExpect(jsonPath("$.priority").value("HIGH"))
                    .andExpect(jsonPath("$.cityId").value("city_bcn"))
                    .andExpect(jsonPath("$.location.lat").exists())
                    .andExpect(jsonPath("$.images[0].id").value("img-001"))
                    .andExpect(jsonPath("$.images[0].url").value("https://example.com/image.jpg"))
                    .andExpect(jsonPath("$.images[0].thumbnailUrl").value("https://example.com/thumb.jpg"))
                    .andExpect(jsonPath("$.images[0].mimeType").value("image/jpeg"))
                    .andExpect(jsonPath("$.images[0].sizeKb").value(120))
                    .andExpect(jsonPath("$.plannedActions").isArray())
                    .andExpect(jsonPath("$.statusHistory").isArray())
                    .andExpect(jsonPath("$.statusHistory[0].id").value("hist-001"))
                    .andExpect(jsonPath("$.statusHistory[0].fromStatus").value("NEW"))
                    .andExpect(jsonPath("$.statusHistory[0].toStatus").value("UNDER_REVIEW"))
                    .andExpect(jsonPath("$.statusHistory[0].changedAt").exists())
                    .andExpect(jsonPath("$.statusHistory[0].changedBy").doesNotExist())
                    .andExpect(jsonPath("$.statusHistory[0].reason").doesNotExist())
                    .andExpect(jsonPath("$.reporter").doesNotExist())
                    .andExpect(jsonPath("$.reporterId").doesNotExist())
                    .andExpect(jsonPath("$.reporterDisplayName").doesNotExist())
                    .andExpect(jsonPath("$.images[0].publicId").doesNotExist());
        }

        @Test
        @DisplayName("should return 404 when incident id does not exist")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/incidents/{id}", "non-existent-id"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Incident not found"));
        }
    }

    @Nested
    @DisplayName("Pagination defaults")
    class PaginationDefaults {

        @Test
        @DisplayName("should return first page of 10 incidents sorted by createdAt DESC when no params provided")
        void defaultPagination() throws Exception {
            for (int i = 0; i < 15; i++) {
                createIncident("Incident " + i, IncidentCategory.POTHOLE, IncidentStatus.NEW,
                        IncidentPriority.MEDIUM, "bcn-001");
            }

            mockMvc.perform(get("/api/incidents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(15))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(false))
                    .andExpect(jsonPath("$.content.length()").value(10))
                    .andExpect(jsonPath("$.sort[0].field").value("createdAt"))
                    .andExpect(jsonPath("$.sort[0].direction").value("DESC"));
        }

        @Test
        @DisplayName("should return empty content with correct metadata when no incidents exist")
        void emptyResult() throws Exception {
            mockMvc.perform(get("/api/incidents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(true))
                    .andExpect(jsonPath("$.content.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Page and size combinations")
    class PageSizeCombinations {

        @Test
        @DisplayName("should return second page with custom page size")
        void pageOneSizeFive() throws Exception {
            for (int i = 0; i < 12; i++) {
                createIncident("Incident " + i, IncidentCategory.POTHOLE, IncidentStatus.NEW,
                        IncidentPriority.MEDIUM, "bcn-001");
            }

            mockMvc.perform(get("/api/incidents")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(12))
                    .andExpect(jsonPath("$.totalPages").value(3))
                    .andExpect(jsonPath("$.first").value(false))
                    .andExpect(jsonPath("$.last").value(false))
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        @DisplayName("should return last page with fewer items")
        void lastPage() throws Exception {
            for (int i = 0; i < 12; i++) {
                createIncident("Incident " + i, IncidentCategory.POTHOLE, IncidentStatus.NEW,
                        IncidentPriority.MEDIUM, "bcn-001");
            }

            mockMvc.perform(get("/api/incidents")
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(12))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.first").value(false))
                    .andExpect(jsonPath("$.last").value(true))
                    .andExpect(jsonPath("$.content.length()").value(2));
        }
    }

    @Nested
    @DisplayName("Sorting")
    class Sorting {

        @Test
        @DisplayName("should sort by createdAt ascending when requested")
        void sortByCreatedAtAsc() throws Exception {
            Instant now = Instant.now();
            for (int i = 0; i < 3; i++) {
                Incident incident = new Incident();
                incident.setTitle("Incident " + i);
                incident.setDescription("Desc");
                incident.setCategory(IncidentCategory.POTHOLE);
                incident.setStatus(IncidentStatus.NEW);
                incident.setPriority(IncidentPriority.MEDIUM);
                incident.setCityId("bcn-001");
                incident.setReporter(testUser);
                incident.setLat(41.0);
                incident.setLng(2.0);
                incident.setGeohash("test");
                incident.setImages(List.of());
                incident.setStatusHistory(List.of());
                incident.onCreate();
                incident.setCreatedAt(now.plus(i, ChronoUnit.HOURS));
                incident.setUpdatedAt(now.plus(i, ChronoUnit.HOURS));
                incidentRepository.save(incident);
            }

            mockMvc.perform(get("/api/incidents")
                            .param("sort", "createdAt,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sort[0].field").value("createdAt"))
                    .andExpect(jsonPath("$.sort[0].direction").value("ASC"))
                    .andExpect(jsonPath("$.content[0].title").value("Incident 0"));
        }

        @Test
        @DisplayName("should fall back to createdAt DESC when sort field is invalid")
        void invalidSortFieldFallsBack() throws Exception {
            createIncident("Test", IncidentCategory.POTHOLE, IncidentStatus.NEW,
                    IncidentPriority.MEDIUM, "bcn-001");

            mockMvc.perform(get("/api/incidents")
                            .param("sort", "nonexistentField,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sort[0].field").value("createdAt"))
                    .andExpect(jsonPath("$.sort[0].direction").value("DESC"));
        }
    }

    @Nested
    @DisplayName("Filter + pagination combined")
    class FilterWithPagination {

        @Test
        @DisplayName("should apply filters and return paginated results")
        void filterAndPaginate() throws Exception {
            for (int i = 0; i < 8; i++) {
                createIncident("Pothole " + i, IncidentCategory.POTHOLE, IncidentStatus.NEW,
                        IncidentPriority.MEDIUM, "bcn-001");
            }
            for (int i = 0; i < 5; i++) {
                createIncident("Light " + i, IncidentCategory.LIGHTING, IncidentStatus.UNDER_REVIEW,
                        IncidentPriority.HIGH, "bcn-001");
            }

            mockMvc.perform(get("/api/incidents")
                            .param("category", "LIGHTING")
                            .param("page", "0")
                            .param("size", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.content.length()").value(3))
                    .andExpect(jsonPath("$.content[0].category").value("LIGHTING"))
                    .andExpect(jsonPath("$.content[1].category").value("LIGHTING"))
                    .andExpect(jsonPath("$.content[2].category").value("LIGHTING"));
        }

        @Test
        @DisplayName("should apply freetext search with pagination")
        void freeTextSearchPaginated() throws Exception {
            createIncident("Broken street light", IncidentCategory.LIGHTING, IncidentStatus.NEW,
                    IncidentPriority.MEDIUM, "bcn-001");
            createIncident("Pothole on Main", IncidentCategory.POTHOLE, IncidentStatus.NEW,
                    IncidentPriority.MEDIUM, "bcn-001");
            createIncident("Another light issue", IncidentCategory.LIGHTING, IncidentStatus.UNDER_REVIEW,
                    IncidentPriority.HIGH, "bcn-001");

            mockMvc.perform(get("/api/incidents")
                            .param("q", "light")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content.length()").value(2));
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("should return 400 when page is negative")
        void negativePage() throws Exception {
            mockMvc.perform(get("/api/incidents")
                            .param("page", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value("page must not be negative"));
        }

        @Test
        @DisplayName("should return 400 when size is zero or negative")
        void zeroSize() throws Exception {
            mockMvc.perform(get("/api/incidents")
                            .param("size", "0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value("size must be greater than 0"));
        }

        @Test
        @DisplayName("should clamp size to 50 when exceeding maximum")
        void maxSizeClamped() throws Exception {
            for (int i = 0; i < 60; i++) {
                createIncident("Incident " + i, IncidentCategory.POTHOLE, IncidentStatus.NEW,
                        IncidentPriority.MEDIUM, "bcn-001");
            }

            mockMvc.perform(get("/api/incidents")
                            .param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(50))
                    .andExpect(jsonPath("$.content.length()").value(50));
        }
    }

    @Nested
    @DisplayName("Response structure")
    class ResponseStructure {

        @Test
        @DisplayName("should include all pagination metadata fields")
        void paginationMetadata() throws Exception {
            createIncident("Test Incident", IncidentCategory.LIGHTING, IncidentStatus.NEW,
                    IncidentPriority.MEDIUM, "city_bcn");

            mockMvc.perform(get("/api/incidents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").isNumber())
                    .andExpect(jsonPath("$.size").isNumber())
                    .andExpect(jsonPath("$.totalElements").isNumber())
                    .andExpect(jsonPath("$.totalPages").isNumber())
                    .andExpect(jsonPath("$.first").isBoolean())
                    .andExpect(jsonPath("$.last").isBoolean())
                    .andExpect(jsonPath("$.sort").isArray());
        }

        @Test
        @DisplayName("should not expose reporter email or private fields in public list")
        void noPrivateFields() throws Exception {
            createIncident("Test Incident", IncidentCategory.LIGHTING, IncidentStatus.NEW,
                    IncidentPriority.MEDIUM, "city_bcn");

            mockMvc.perform(get("/api/incidents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").exists())
                    .andExpect(jsonPath("$.content[0].title").exists())
                    .andExpect(jsonPath("$.content[0].category").exists())
                    .andExpect(jsonPath("$.content[0].status").exists())
                    .andExpect(jsonPath("$.content[0].priority").exists())
                    .andExpect(jsonPath("$.content[0].cityId").exists())
                    .andExpect(jsonPath("$.content[0].location").exists())
                    .andExpect(jsonPath("$.content[0].createdAt").exists())
                    .andExpect(jsonPath("$.content[0].updatedAt").exists())
                    .andExpect(jsonPath("$.content[0].reporter").doesNotExist())
                    .andExpect(jsonPath("$.content[0].description").doesNotExist())
                    .andExpect(jsonPath("$.content[0].images").doesNotExist())
                    .andExpect(jsonPath("$.content[0].statusHistory").doesNotExist())
                    .andExpect(jsonPath("$.content[0].plannedActions").doesNotExist());
        }

        @Test
        @DisplayName("should include thumbnailUrl when incident has images")
        void thumbnailUrlPresent() throws Exception {
            Incident incident = new Incident();
            incident.setTitle("With image");
            incident.setDescription("Has an image");
            incident.setCategory(IncidentCategory.LIGHTING);
            incident.setStatus(IncidentStatus.NEW);
            incident.setPriority(IncidentPriority.MEDIUM);
            incident.setCityId("bcn-001");
            incident.setReporter(testUser);
            incident.setLat(41.0);
            incident.setLng(2.0);
            incident.setGeohash("test");

            IncidentImage image = new IncidentImage();
            image.setId("img-001");
            image.setUrl("https://example.com/image.jpg");
            image.setThumbnailUrl("https://example.com/thumb.jpg");
            image.setPublicId("pub-001");
            image.setMimeType("image/jpeg");
            image.setSizeKb(100);
            image.setIncident(incident);
            incident.setImages(List.of(image));
            incident.setStatusHistory(List.of());
            incident.onCreate();
            incidentRepository.save(incident);

            mockMvc.perform(get("/api/incidents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].thumbnailUrl").value("https://example.com/thumb.jpg"));
        }
    }
}
