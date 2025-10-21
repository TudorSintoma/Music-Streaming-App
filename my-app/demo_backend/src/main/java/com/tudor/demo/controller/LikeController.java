package com.tudor.demo.controller;

import com.tudor.demo.dto.LikeDTO;
import com.tudor.demo.model.Like;
import com.tudor.demo.service.LikeService;
import com.tudor.demo.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private SongRepository songRepository;

    @GetMapping
    public List<Like> getAll() {
        return likeService.getAllLikes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Like> getById(@PathVariable Integer id) {
        return likeService.getLikeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@PathVariable int userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        likeService.deleteLike(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Integer> toggleLike(@RequestBody LikeDTO likeDTO) {
        likeService.toggleLike(likeDTO.getUserId(), likeDTO.getSongId());
        var song = songRepository.findById(likeDTO.getSongId()).orElseThrow();
        return ResponseEntity.ok(song.getLikes());
    }
}
