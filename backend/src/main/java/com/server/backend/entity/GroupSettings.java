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

    private String movieProgress;

    @ManyToOne
    private Translation selectedTranslation;

    @JsonIgnore
    @OneToOne(mappedBy = "groupSettings")
    private Group group;

    public GroupSettings(Movie selectedMovie, String movieProgress,
                         Translation selectedTranslation) {
        this.selectedMovie = selectedMovie;
        this.movieProgress = movieProgress;
        this.selectedTranslation = selectedTranslation;
    }
}
