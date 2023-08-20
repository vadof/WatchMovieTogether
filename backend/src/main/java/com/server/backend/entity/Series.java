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
public class Series {

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SeriesTranslation> seriesTranslations = new ArrayList<>();

    public void addSearch() {
        this.searches++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Series that)) return false;

        return Objects.equals(link, that.link) &&
                Objects.equals(name, that.name) &&
                seriesTranslationsEqual(that);
    }


    private boolean seriesTranslationsEqual(Series otherSeries) {
        boolean equal = seriesTranslations.size() == otherSeries.seriesTranslations.size();
        if (equal) {
            for (int i = 0; i < seriesTranslations.size(); i++) {
                if (!seriesTranslations.get(i).equals(otherSeries.seriesTranslations.get(i))) {
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
        for (SeriesTranslation st : seriesTranslations) {
            result = 31 * result + st.hashCode();
        }
        return Objects.hash(link, name) + result;
    }
}
