package com.server.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String firstname;

    @Column(nullable = false)
    @NotBlank
    private String lastname;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String username;

    @Column(unique = true)
    @NotBlank
    @Email
    private String email;

    @Size(min = 6)
    private String password;

    @DateTimeFormat(pattern="dd/MM/yyyy")
    private LocalDate registerDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

    @PrePersist
    private void setDate() {
        this.registerDate = LocalDate.now();
    }
}
