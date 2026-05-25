package com.urbancore.urbancore_api.auth.service;

import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.auth.entity.UserRole;
import com.urbancore.urbancore_api.auth.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User syncUser(Jwt jwt) {
        String firebaseUid = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setFirebaseUid(firebaseUid);
                    newUser.setEmail(email);
                    newUser.setRole(UserRole.ROLE_CITIZEN);

                    return userRepository.save(newUser);
                });
    }
}