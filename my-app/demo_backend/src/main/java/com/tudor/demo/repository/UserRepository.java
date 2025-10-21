package com.tudor.demo.repository;

import com.tudor.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByFullName(String fullName);

    Optional<User> findByUserId(int userId);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE ?1% OR u.fullName LIKE %?1")
    List<User> findByFullNameApproximate(String fullName);

    List<User> findByFullNameStartingWithOrFullNameEndingWith(String start, String end);
}
