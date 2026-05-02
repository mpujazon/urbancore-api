package com.urbancore.urbancore_api.services;

import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.models.UserRole;
import com.urbancore.urbancore_api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Jwt jwt) {
        String firebaseUid = jwt.getSubject();

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User profile not found"
                ));
    }
}