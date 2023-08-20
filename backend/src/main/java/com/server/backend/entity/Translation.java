package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Resolution> resolutions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Translation that)) return false;

        return Objects.equals(name, that.name) &&
                this.resolutionsEqual(that);
    }

    private boolean resolutionsEqual(Translation otherTranslation) {
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

    @Override
    public int hashCode() {
        int result = 17;
        for (Resolution resolution : resolutions) {
            result = 31 * result + resolution.hashCode();
        }
        return Objects.hash(name) + result;
    }
}
