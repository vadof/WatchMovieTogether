package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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
    @Column(unique = true)
    private String link;

    @NotBlank
    private String name;

    @JsonIgnore
    private int searches = 1;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Translation> translations = new ArrayList<>();

    public void addSearch() {
        this.searches++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie otherMovie = (Movie) o;
        return Objects.equals(this.id, otherMovie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
