package com.urbancore.urbancore_api.auth.service;

import com.urbancore.urbancore_api.auth.entity.User;
import com.urbancore.urbancore_api.auth.entity.UserRole;
import com.urbancore.urbancore_api.auth.repository.UserRepository;
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