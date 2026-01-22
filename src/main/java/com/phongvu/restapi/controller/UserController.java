package com.phongvu.restapi.controller;

import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    User createUser(@RequestBody @Valid UserCreationRequest request) {
        return userService.createRequestUser(request);
    }

    @GetMapping()
    List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping(path = "/{id}")
    Optional<User> getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping(path = "/{id}")
    Optional<User> updateUser(@RequestBody UserUpdateRequest request,
                              @PathVariable String id) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping(path = "/{id}")
    String removeUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "Delete Successfully";
    }

}
