package com.server.backend.controllers;

import com.server.backend.exceptions.UserRegisterException;
import com.server.backend.requests.AuthenticationRequest;
import com.server.backend.requests.RegisterRequest;
import com.server.backend.responses.AuthenticationResponse;
import com.server.backend.responses.ErrorResponse;
import com.server.backend.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse res = authenticationService.register(request);
            LOG.info("User saved to database");
            return ResponseEntity.ok(res);
        } catch (UserRegisterException e) {
            LOG.error("Error adding user to database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
