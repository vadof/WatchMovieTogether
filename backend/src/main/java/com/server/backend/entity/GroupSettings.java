package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupSettings {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Movie selectedMovie;

    @ManyToOne
    private Translation selectedTranslation;

    @ManyToOne
    private Series selectedSeries;

    @ManyToOne
    private SeriesTranslation selectedSeriesTranslation;

    @JsonIgnore
    @OneToOne(mappedBy = "groupSettings")
    private Group group;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> usersWithPrivileges = new HashSet<>();

    public void setMovieForGroup(Movie movie, Translation selectedTranslation) {
        this.selectedSeries = null;
        this.selectedSeriesTranslation = null;

        this.selectedMovie = movie;
        this.selectedTranslation = selectedTranslation;
    }

    public void setSeriesForGroup(Series series, SeriesTranslation seriesTranslation) {
        this.selectedMovie = null;
        this.selectedTranslation = null;

        this.selectedSeries = series;
        this.selectedSeriesTranslation = seriesTranslation;
    }
}
