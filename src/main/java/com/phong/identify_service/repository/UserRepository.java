package com.phong.identify_service.repository;

import com.phong.identify_service.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {
    int insertUser(User user);
    int updateUser(User user);
    Optional<User> findUserById(String id);
    int deleteById(String id);
    List<User> findAll();
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> searchByName(String searchTerm);
}

