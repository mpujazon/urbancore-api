package com.urbancore.urbancore_api.seed.factory;

import com.urbancore.urbancore_api.models.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSeedFactory {

    public List<UserSeed> createUsers() {
        return List.of(
                new UserSeed("admin-barcelona", "seed-admin-barcelona", "admin.barcelona@urbancore.demo", UserRole.ROLE_ADMIN, "barcelona"),
                new UserSeed("admin-lhospitalet", "seed-admin-lhospitalet", "admin.lhospitalet@urbancore.demo", UserRole.ROLE_ADMIN, "lhospitalet"),
                new UserSeed("admin-santa-coloma", "seed-admin-santa-coloma", "admin.santacoloma@urbancore.demo", UserRole.ROLE_ADMIN, "santa-coloma"),
                new UserSeed("admin-terrassa", "seed-admin-terrassa", "admin.terrassa@urbancore.demo", UserRole.ROLE_ADMIN, "terrassa"),
                new UserSeed("citizen-lucas", "seed-citizen-lucas", "lucas.martin@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-sofia", "seed-citizen-sofia", "sofia.alonso@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-emma", "seed-citizen-emma", "emma.rodriguez@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-daniel", "seed-citizen-daniel", "daniel.ortega@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-noah", "seed-citizen-noah", "noah.garcia@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-laia", "seed-citizen-laia", "laia.serra@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-marc", "seed-citizen-marc", "marc.vila@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-ines", "seed-citizen-ines", "ines.pujol@urbancore.demo", UserRole.ROLE_CITIZEN, null),
                new UserSeed("citizen-mikeel", "FRDW7S3qPabRlDAr8TmvLiSh4Ov2", "mikeel2711@gmail.com", UserRole.ROLE_CITIZEN, null),
                new UserSeed("admin-mpujazon", "95WyDYhBoHR1mSRL1BfN64PPTVs2", "mpujazoncardenas@gmail.com", UserRole.ROLE_ADMIN, "barcelona")
        );
    }

    public record UserSeed(String key, String firebaseUid, String email, UserRole role, String cityRef) {
    }
}
