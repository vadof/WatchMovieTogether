package com.server.backend.services;

import com.server.backend.entity.Role;
import com.server.backend.entity.User;
import com.server.backend.exceptions.UserRegisterException;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.UserRepository;
import com.server.backend.requests.AuthenticationRequest;
import com.server.backend.requests.RegisterRequest;
import com.server.backend.responses.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws UserRegisterException {
        validateEmail(request.getEmail());
        validateUniqueUsername(request.getUsername());

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    private void validateUniqueUsername(String username) throws UserRegisterException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserRegisterException("Username already exists");
        }
    }

    private void validateEmail(String email) throws UserRegisterException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserRegisterException("Email already in use");
        } else if (!Pattern.matches("^(.+)@(\\S+)$", email)) {
            throw new UserRegisterException("Invalid email");
        }
    }

}
