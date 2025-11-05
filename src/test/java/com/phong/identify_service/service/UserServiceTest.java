package com.phong.identify_service.service;

import com.phong.identify_service.dto.request.UserCreationRequest;
import com.phong.identify_service.dto.response.UserResponse;
import com.phong.identify_service.entity.User;
import com.phong.identify_service.exception.AppException;
import com.phong.identify_service.mapper.UserMapper;
import com.phong.identify_service.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;
    private User user;

    @BeforeEach
    void initData(){
        dob = LocalDate.of(1990, 1, 1);
        request = UserCreationRequest.builder()
                .username("phong1")
                .firstName("huynh")
                .lastName("phong")
                .password("test123@")
                .dob(dob)
                .build();
        userResponse = UserResponse.builder()
                .id("64f39c91c953")
                .username("phong1")
                .firstName("huynh")
                .lastName("phong")
                .dob(dob)
                .build();
        user = User.builder()
                .id("64f39c91c953")
                .username("phong1")
                .firstName("huynh")
                .lastName("phong")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        //GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);


        //WHEN
        var response = userService.createUser(request);

        //THEN
        Assertions.assertThat(response.getId()).isEqualTo("64f39c91c953");
        Assertions.assertThat(response.getUsername()).isEqualTo("phong1");
    }

    @Test
    void createUser_userExisted_fail() {
        //GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(true);

        //WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(400);
    }

    @Test
    void getUserById_valid_success() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));

        var response = userService.getUserById("64f39c91c953");
        Assertions.assertThat(response.getUsername()).isEqualTo("phong1");
    }

    @Test
    void getUserById_notExisted_fail() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(RuntimeException.class, () -> userService.getUserById("test"));
        Assertions.assertThat(exception.getMessage()).isEqualTo("User not found");

        // Kiểm tra xem Repository đã được gọi
        Mockito.verify(userRepository, Mockito.times(1)).findById("test");
    }
}
