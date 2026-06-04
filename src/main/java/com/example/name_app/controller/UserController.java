package com.example.name_app.controller;

import com.example.name_app.dto.UserRequestDTO;
import com.example.name_app.entity.UserProfile;
import com.example.name_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allows your front-end HTML page to talk to this API safely
public class UserController {
    private final UserService userService;

    // Dependency injection via constructor
    public UserController(UserService userService){
        this.userService = userService;

    }

    // 1. GET: Fetch all profiles from the database
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllUsers(){
        List<UserProfile> allUsersList = userService.getAllUsersProfiles();
        return ResponseEntity.status(HttpStatus.OK).body(allUsersList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable Long id){
        UserProfile user = userService.getUserProfileById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    public ResponseEntity<UserProfile> createUser(@RequestBody UserRequestDTO request){
        UserProfile newUser = userService.saveUserProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }


}
