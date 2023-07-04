package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Resolution> resolutions = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<VoiceOver> voiceOvers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "currentMovie")
    private Set<Group> groups;

}
