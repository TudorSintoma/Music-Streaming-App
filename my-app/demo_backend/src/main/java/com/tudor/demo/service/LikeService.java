package com.tudor.demo.service;

import com.tudor.demo.dto.LikeDTO;
import com.tudor.demo.model.Like;
import com.tudor.demo.model.Song;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.LikeRepository;
import com.tudor.demo.repository.SongRepository;
import com.tudor.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Optional<Like> getLikeById(Integer id) {
        return likeRepository.findById(id);
    }

    public List<Like> getLikesByUserId(int userId) {
        return Optional.of(userId)
                .flatMap(userRepository::findByUserId)
                .map(likeRepository::findByUser)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
    }

    public void toggleLike(int userId, int songId) {
        Optional.of(likeRepository.existsByUserIdAndSongId(userId, songId))
                .filter(Boolean::booleanValue)
                .ifPresentOrElse(
                        exists -> removeLike(userId, songId),
                        () -> {
                            var user = userRepository.findById(userId).orElseThrow();
                            var song = songRepository.findById(songId).orElseThrow();

                            var like = new Like();
                            like.setUserId(userId);
                            like.setSongId(songId);
                            like.setUser(user);
                            like.setSong(song);
                            likeRepository.save(like);

                            song.setLikes(song.getLikes() + 1);
                            songRepository.save(song);
                        }
                );
    }

    public void removeLike(int userId, int songId) {
        Optional.ofNullable(likeRepository.findByUserIdAndSongId(userId, songId))
                .ifPresent(like -> {
                    likeRepository.delete(like);
                    songRepository.findById(songId).ifPresent(song -> {
                        song.setLikes(Math.max(0, song.getLikes() - 1));
                        songRepository.save(song);
                    });
                });
    }

    public Like createLike(LikeDTO dto) {
        return Optional.of(dto)
                .flatMap(d ->
                        userRepository.findById(d.getUserId())
                                .flatMap(user ->
                                        songRepository.findById(d.getSongId())
                                                .map(song -> {
                                                    var like = new Like();
                                                    like.setUser(user);
                                                    like.setSong(song);
                                                    return likeRepository.save(like);
                                                })
                                )
                ).orElseThrow(() -> new IllegalArgumentException("Invalid user or song"));
    }

    public void deleteLike(Integer id) {
        likeRepository.deleteById(id);
    }
}
