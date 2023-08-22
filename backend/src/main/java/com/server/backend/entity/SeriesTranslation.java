package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeriesTranslation {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Season> seasons = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Resolution> resolutions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesTranslation that)) return false;

        return Objects.equals(name, that.name) &&
                this.resolutionsEqual(that) &&
                this.seasonsEqual(that);
    }

    private boolean resolutionsEqual(SeriesTranslation otherTranslation) {
        boolean equal = resolutions.size() == otherTranslation.resolutions.size();
        if (equal) {
            for (int i = 0; i < resolutions.size(); i++) {
                if (!resolutions.get(i).equals(otherTranslation.resolutions.get(i))) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    private boolean seasonsEqual(SeriesTranslation otherTranslation) {
        boolean equal = seasons.size() == otherTranslation.seasons.size();
        if (equal) {
            for (int i = 0; i < seasons.size(); i++) {
                if (!seasons.get(i).equals(otherTranslation.seasons.get(i))) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        int result = 17;

        for (Resolution resolution : resolutions) {
            result = 31 * result * resolution.hashCode();
        }

        for (Season season : seasons) {
            result = 31 * result * season.hashCode();
        }

        return Objects.hash(name) + result;
    }
}
