package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.constraint.Role;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.mapper.UserMapper;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.service.UserService;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with encoded password and default USER role.
     *
     * @param request the user creation request
     * @return the created user response
     * @throws AppException if username already exists
     */
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);

        return userMapper.toUserResponse(userRepo.save(user));
    }

    /**
     * Retrieves all users. Requires ADMIN role.
     *
     * @return list of all user responses
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(authority -> log.info(authority.getAuthority()));

        return userRepo.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getPage(int page, int size) {
//        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return userRepo.findAll(PageRequest.of(page, size)).map(userMapper::toUserResponse);
    }

    /**
     * Retrieves a user by ID. Only the user themselves can view.
     *
     * @param id the user ID
     * @return the user response
     * @throws AppException if user not found
     */
    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return the user response for the current user
     * @throws AppException if user not found or not authenticated
     */
    public UserResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        User user = userRepo.findUserByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    /**
     * Updates a user by ID. Requires ADMIN role. Password is encoded if provided.
     *
     * @param id        the user ID
     * @param request   the update request
     * @return          the updated user response
     * @throws AppException if user not found
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);

        if (request.getPassword() != null && !request.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepo.save(user));
    }

    /**
     * Deletes a user by ID. Requires ADMIN role.
     *
     * @param id the user ID
     * @throws AppException if user not found
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) throw new AppException(ErrorCode.USER_NOT_FOUND);
        userRepo.deleteById(id);
    }
}
