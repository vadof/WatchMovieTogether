package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


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

    private String videoLink;
    private String movieProgress;

    @ManyToOne
    private Translation selectedTranslation;

    @ManyToOne
    private Resolution selectedResolution;

    @JsonIgnore
    @OneToOne(mappedBy = "groupSettings")
    private Group group;

    public GroupSettings(Movie selectedMovie, String videoLink, String movieProgress,
                         Translation selectedTranslation, Resolution selectedResolution) {
        this.selectedMovie = selectedMovie;
        this.videoLink = videoLink;
        this.movieProgress = movieProgress;
        this.selectedTranslation = selectedTranslation;
        this.selectedResolution = selectedResolution;
    }
}
