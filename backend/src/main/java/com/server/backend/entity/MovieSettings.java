package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieSettings {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Movie selectedMovie;

    @ManyToOne
    private Translation selectedTranslation;

    @JsonIgnore
    @OneToOne(mappedBy = "movieSettings")
    private GroupSettings groupSettings;

}
