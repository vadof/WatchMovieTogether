package com.server.backend.controllers;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.*;
import com.server.backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final GroupRepository groupRepository;

//    @PostMapping()
//    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
////        System.out.println('t');
////        System.out.println(token.substring(7));
////        String username = jwtService.extractUsername(token.substring(7));
////        User user = userRepository.findByUsername(username).orElseThrow();
//
////        Group group = groupRepository.findById(1L).get();
////        user.getGroups().add(group);
////        userRepository.save(user);
////        VoiceOver voiceOver = new VoiceOver();
////        voiceOver.setName("Lostfilm");
////        voiceOverRepository.save(voiceOver);
////
////        Resolution resolution = new Resolution();
////        resolution.setValue("1080");
////        resolutionRepository.save(resolution);
////
////        Movie movie = new Movie();
////        movie.setLink("Https/");
////        movie.getResolutions().add(resolution);
////        movie.getVoiceOvers().add(voiceOver);
////        movieRepository.save(movie);
////
////        Group group = new Group();
////        group.setName("New group");
////        group.setCurrentMovie(movie);
////        group.getUsers().add(user);
////        group.setMovieProgress(1000L);
////        groupRepository.save(group);
//
////        user.getGroups().add(group);
////        userRepository.save(user);
//
//        return ResponseEntity.ok(user);
//    }

//    @PostMapping("/group")
//    public ResponseEntity<List<Group>> getGroupInfo(@RequestHeader("Authorization") String token) {
////        System.out.println('t');
////        System.out.println(token.substring(7));
//        return ResponseEntity.ok(userService.getUserGroups(token));
//    }

    @PostMapping("/groups")
    public ResponseEntity<List<Group>> getUserGroups(@RequestHeader("Authorization") String token) {
        System.out.println(userService.getUserGroups(token));
        return ResponseEntity.ok(userService.getUserGroups(token));
    }

}
