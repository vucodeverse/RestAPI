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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     *
     * @param request
     * @return
     */
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {

        if (userRepo.existsByUsername(request.getUsername()))
            throw new AppException(ApiMessage.USER_EXISTED);

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
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
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
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND)));
    }


    /**
     *
     *
     * @return
     */
    public UserResponse getProfile() {
        var context = SecurityContextHolder.getContext();
        String name = Objects.requireNonNull(context.getAuthentication()).getName();

        User user = userRepo.findUserByUsername(name).
                orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);

    }

    /**
     *
     * @param id
     * @param request
     * @return
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepo.save(user));
    }

    /**
     *
     * @param id
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String id) {

        if (!userRepo.existsById(id)) {
            throw new AppException(ApiMessage.USER_NOT_FOUND);
        }

        userRepo.deleteById(id);

    }
}
