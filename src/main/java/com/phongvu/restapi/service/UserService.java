package com.phongvu.restapi.service;

import com.phongvu.restapi.constants.ApiMessage;
import com.phongvu.restapi.constants.Role;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.mapper.UserMapper;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     *
     * @param request
     * @return
     */
    public UserResponse createRequestUser(UserCreationRequest request) {

        if (userRepo.existsByUsername(request.getUsername()))
            throw new AppException(ApiMessage.USER_EXITED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        user.setRoles(roles);

        return userMapper.toUserResponse(userRepo.save(user));
    }

    /**
     *
     * @return
     */
    public List<UserResponse> getAllUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority
                -> log.info(grantedAuthority.getAuthority()));


        return userRepo.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    /**
     *
     * @param id
     * @return
     */
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND)));
    }

    /**
     *
     * @param id
     * @param request
     * @return
     */
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepo.save(user));
    }

    public void deleteUser(String id) {

        if (getUserById(id) == null) {
            throw new AppException(ApiMessage.USER_NOT_FOUND);
        }
        userRepo.deleteById(id);

    }
}
