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
public class Resolution {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String value;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "resolutions")
    private List<Movie> movie;

    @JsonIgnore
    @OneToMany(mappedBy = "resolution")
    private List<SelectedMovieSettings> selectedMovies;
    public Resolution(String value) {
        this.value = value;
    }
}
