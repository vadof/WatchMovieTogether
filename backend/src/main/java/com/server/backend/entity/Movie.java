package com.server.backend.entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String link;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Resolution> resolutions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<VoiceOver> voiceOvers = new HashSet<>();

}
