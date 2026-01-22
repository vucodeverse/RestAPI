package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public User createRequestUser(UserCreationRequest request) {
        User user = new User();

        if (userRepo.existsByUsername(request.getUsername()))
            throw new RuntimeException("UserName is exited!!");
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setDob(request.getDob());

        return userRepo.save(user);
    }

    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    public Optional<User> updateUser(String id, UserUpdateRequest request) {
        Optional<User> optionalUser = getUserById(id);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setDob(request.getDob());

        userRepo.save(user);

        return Optional.of(user);
    }

    public void deleteUser(String id) {

        if (getUserById(id).isEmpty()) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);

//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        userRepo.delete(user);
    }
}
