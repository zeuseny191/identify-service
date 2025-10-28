package com.phong.identify_service.repository;

import com.phong.identify_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "UPPER(u.firstName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
            "UPPER(u.lastName) LIKE UPPER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByName(String searchTerm, Pageable pageable);
}
