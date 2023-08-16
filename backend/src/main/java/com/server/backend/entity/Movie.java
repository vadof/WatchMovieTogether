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
        if (!(o instanceof Movie that)) return false;

        return Objects.equals(link, that.link) &&
                Objects.equals(name, that.name) &&
                translationsEqual(that);
    }

    private boolean translationsEqual(Movie otherMovie) {
        if (translations.size() == otherMovie.translations.size()) {
            for (int i = 0; i < translations.size(); i++) {
                if (!translations.get(i).equals(otherMovie.translations.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, name, translations);
    }
}
