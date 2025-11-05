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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    private static final String REPORT_TEMPLATE_NAME = "reports/user_report.jrxml";

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

    public byte[] exportReport(String format, String searchTerm) throws JRException, FileNotFoundException {
        // 1. Lấy dữ liệu
        // Lưu ý: getUsers() trả về List<User> - đây là entity của bạn.
        // Đảm bảo các trường trong entity User khớp với các trường trong file JRXML.
        List<User> data = userRepository.searchByName(searchTerm);

        JasperReport jasperReport;

        // 2. Tải và biên dịch template JRXML một cách an toàn từ ClassPath
        try (InputStream inputStream = new FileInputStream("src/main/resources/reports/user_report.jrxml")) {

            // Biên dịch template
            jasperReport = JasperCompileManager.compileReport(inputStream);

        } catch (Exception e) {
            log.error("Lỗi khi tải hoặc biên dịch template JasperReports: {}", e.getMessage());
            // Ném ngoại lệ JRException để xử lý ở tầng Controller
            throw new JRException("Không thể tạo báo cáo do lỗi template.", e);
        }

        // 3. Đặt nguồn dữ liệu
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // 4. Đặt các tham số (nếu có)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TieuDeBaoCao", "Danh Sách Người Dùng Công Ty ABC");

        // 5. Điền dữ liệu vào báo cáo
        // Vì không sử dụng DataSource/Connection, ta dùng dataSource collection
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 6. Xuất báo cáo theo định dạng yêu cầu
        if (format.equalsIgnoreCase("pdf")) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }

        // Trả về null nếu định dạng không được hỗ trợ
        return null;
    }
}
