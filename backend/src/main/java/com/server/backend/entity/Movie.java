package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Movie {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String link;

    @NotBlank
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Resolution> resolutions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Translation> translations = new HashSet<>();

    @OneToMany(mappedBy = "selectedMovie")
    private Set<GroupSettings> groupSettings = new HashSet<>();
}
