package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

//    @ManyToOne
//    private User admin;

    @ManyToOne
    private Movie currentMovie;

    private Long movieProgress;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", movieProgress=" + movieProgress +
                '}';
    }

}
