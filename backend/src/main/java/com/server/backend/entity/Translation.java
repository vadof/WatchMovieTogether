package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Translation {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotBlank
    private String value;

    @JsonIgnore
    @OneToMany(mappedBy = "translation")
    private List<SelectedMovieSettings> selectedMovieSettings;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "translations")
    private List<Movie> movies;

    public Translation(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
