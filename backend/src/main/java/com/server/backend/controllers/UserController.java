package com.server.backend.controllers;

import com.server.backend.exceptions.UserRegisterException;
import com.server.backend.forms.RegisterForm;
import com.server.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterForm form) {
        try {
            userService.saveUser(form);
            LOG.info("User saved to database");
            return ResponseEntity.status(HttpStatus.CREATED).body("User created");
        } catch (UserRegisterException e) {
            LOG.error("Error adding user to database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
