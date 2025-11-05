package com.phong.identify_service.controller;

import com.github.pagehelper.PageInfo;
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
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
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
    ApiResponse<PageInfo<User>> searchUsers(@RequestParam(value = "name", required = false) String searchTerm,
                                            @RequestParam(value = "page", required = false) int page,
                                            @RequestParam(value = "size", required = false) int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<PageInfo<User>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.searchByName(searchTerm, pageable));
        return apiResponse;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> downloadReport(@RequestParam(value = "format", required = false) String format,
                                                 @RequestParam(value = "name", required = false) String searchTerm) {
        try {
            // Gọi Service để tạo báo cáo (dạng byte array)
            byte[] reportBytes = userService.exportReport(format, searchTerm);

            if (reportBytes == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Định dạng báo cáo không hợp lệ.".getBytes());
            }

            // Thiết lập Headers cho Response
            HttpHeaders headers = new HttpHeaders();

            // Đặt tên file và loại nội dung
            String fileName = "danh_sach_nguoi_dung." + format.toLowerCase();
            MediaType contentType = MediaType.APPLICATION_PDF; // Giả định là PDF

            if (format.equalsIgnoreCase("pdf")) {
                contentType = MediaType.APPLICATION_PDF;
            } else if (format.equalsIgnoreCase("html")) {
                contentType = MediaType.TEXT_HTML;
                // Thêm logic xử lý HTML nếu cần
            }

            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(contentType);
            headers.setContentLength(reportBytes.length);

            // Trả về file báo cáo
            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy template báo cáo.".getBytes());
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi trong quá trình tạo báo cáo Jasper.".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống không xác định.".getBytes());
        }
    }
}
