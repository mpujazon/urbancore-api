package com.urbancore.urbancore_api.security;

import com.urbancore.urbancore_api.models.User;
import com.urbancore.urbancore_api.repositories.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    public CustomJwtAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String firebaseUid = jwt.getSubject();

        List<SimpleGrantedAuthority> authorities = userRepository.findByFirebaseUid(firebaseUid)
                .map(this::mapUserToAuthorities)
                .orElse(List.of());

        return new JwtAuthenticationToken(jwt, authorities, firebaseUid);
    }

    private List<SimpleGrantedAuthority> mapUserToAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }
}