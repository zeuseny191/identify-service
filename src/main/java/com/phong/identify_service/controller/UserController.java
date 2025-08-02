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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId){
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId){
        userService.deleteUserById(userId);
        return "User has been deleted!";
    }
}
