package com.tudor.demo.controller;

import com.tudor.demo.dto.FollowDTO;
import com.tudor.demo.model.Follow;
import com.tudor.demo.repository.FollowRepository;
import com.tudor.demo.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private FollowService followService;

    private FollowRepository followRepository;

    @GetMapping
    public List<Follow> getAll() {
        return followService.getAllFollows();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Follow> getById(@PathVariable Integer id) {
        return followService.getFollowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/listener/{listenerId}")
    public ResponseEntity<List<Follow>> getFollowsByListenerId(@PathVariable Integer listenerId) {
        List<Follow> follows = followRepository.findByListenerListenerId(listenerId);
        return ResponseEntity.ok(follows);
    }

    @PostMapping
    public Follow create(@RequestBody FollowDTO dto) {
        return followService.createFollow(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Follow> update(@PathVariable Integer id, @RequestBody FollowDTO dto) {
        try {
            return ResponseEntity.ok(followService.updateFollow(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody FollowDTO dto) {
        followService.deleteFollow(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfFollowing(@RequestParam int listenerId, @RequestParam int artistId) {
        boolean isFollowing = followService.isFollowing(listenerId, artistId);
        return ResponseEntity.ok().body(Map.of("isFollowing", isFollowing));
    }
}
