package com.tudor.demo.repository;

import com.tudor.demo.model.Like;
import com.tudor.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByUserIdAndSongId(int userId, int songId);
    List<Like> findByUser(User user);
    Like findByUserIdAndSongId(int userId, int songId);
}
