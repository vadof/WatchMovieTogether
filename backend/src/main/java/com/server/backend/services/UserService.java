package com.server.backend.services;

import com.server.backend.entity.User;
import com.server.backend.exceptions.UserRegisterException;
import com.server.backend.forms.RegisterForm;
import com.server.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(RegisterForm form) throws UserRegisterException {
        try {
            validatePassword(form.getPassword(), form.getConfirmPassword());
            validateUniqueUsername(form.getUsername());
            validateEmail(form.getEmail());

            User user = createUserFromForm(form);
            userRepository.save(user);

            return user;
        } catch (ConstraintViolationException ex) {
            List<String> errors = ex.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .toList();

            throw new UserRegisterException(String.join(", ", errors));
        }
    }

    private void validatePassword(String password, String confirmPassword) throws UserRegisterException {
        if (!password.equals(confirmPassword)) {
            throw new UserRegisterException("Passwords must match");
        }
    }

    private void validateUniqueUsername(String username) throws UserRegisterException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserRegisterException("Username already exists");
        }
    }

    private void validateEmail(String email) throws UserRegisterException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserRegisterException("Email already in use");
        }
    }

    private User createUserFromForm(RegisterForm form) {
        User user = new User();
        user.setFirstname(form.getFirstname());
        user.setLastname(form.getLastname());
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());

        return user;
    }

}
