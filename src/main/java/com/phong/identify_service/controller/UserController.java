package com.phong.identify_service.controller;

import com.phong.identify_service.dto.request.ApiResponse;
import com.phong.identify_service.dto.request.UserCreationRequest;
import com.phong.identify_service.dto.request.UserUpdateRequest;
import com.phong.identify_service.dto.response.UserResponse;
import com.phong.identify_service.entity.User;
import com.phong.identify_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<User>> getUsers(){
        ApiResponse<List<User>> apiResponse = new ApiResponse<>();

        var authentication =  SecurityContextHolder.getContext().getAuthentication();

        log.info("username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.toString()));

        apiResponse.setResult(userService.getUsers());
        return apiResponse;
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserById(userId));
        return apiResponse;
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Object> deleteUser(@PathVariable("userId") String userId){
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        userService.deleteUserById(userId);
        apiResponse.setMessage("User has been deleted!");
        apiResponse.setResult(null);
        return apiResponse;
    }

    @GetMapping("/search")
    ApiResponse<Page<User>> searchUsers(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "page", required = false) int page,
                                        @RequestParam(value = "size", required = false) int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<Page<User>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.searchUsersByName(name, pageable));
        return apiResponse;
    }
}
