package com.server.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String admin;

    @ManyToOne
    private Movie currentMovie;

    private Long movieProgress;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    private Set<User> users = new HashSet<>();

}
