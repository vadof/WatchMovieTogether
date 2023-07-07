package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class SelectedMovieSettings {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String movieLink;
    private Long movieProgress;

    @ManyToOne
    private Translation translation;

    @ManyToOne
    private Resolution resolution;

    @JsonIgnore
    @OneToOne(mappedBy = "selectedMovieSettings")
    private Group group;

}
