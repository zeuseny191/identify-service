package com.phong.identify_service.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phong.identify_service.dto.request.UserCreationRequest;
import com.phong.identify_service.dto.request.UserUpdateRequest;
import com.phong.identify_service.dto.response.UserResponse;
import com.phong.identify_service.entity.User;
import com.phong.identify_service.enums.Role;
import com.phong.identify_service.exception.AppException;
import com.phong.identify_service.exception.ErrorCode;
import com.phong.identify_service.mapper.UserMapper;
import com.phong.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){

        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(Role.USER.name());
        int amount = userRepository.insertUser(user);

        log.info("Số lượng record created: {}", amount);

        return userMapper.toUserResponse(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUserById(String id){
        return userMapper.toUserResponse(userRepository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public PageInfo<User> searchByName(String searchTerm, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize());

        List<User> userList = userRepository.searchByName(searchTerm);

        return new PageInfo<>(userList);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user, request);
        int amount = userRepository.updateUser(user);

        log.info("Số lượng record updated: {}", amount);

        return userMapper.toUserResponse(user);
    }

    public void deleteUserById(String userId){
        int amount = userRepository.deleteById(userId);

        log.info("Số lượng record deleted: {}", amount);
    }
}
