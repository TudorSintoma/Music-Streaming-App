package com.tudor.demo.service;

import com.tudor.demo.dto.FollowDTO;
import com.tudor.demo.model.Artist;
import com.tudor.demo.model.Follow;
import com.tudor.demo.model.Listener;
import com.tudor.demo.repository.ArtistRepository;
import com.tudor.demo.repository.FollowRepository;
import com.tudor.demo.repository.ListenerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final ListenerRepository listenerRepository;
    private final ArtistRepository artistRepository;

    public List<Follow> getAllFollows() {
        return followRepository.findAll();
    }

    public Optional<Follow> getFollowById(Integer id) {
        return followRepository.findById(id);
    }

    public Follow createFollow(FollowDTO dto) {
        return getListenerAndArtist(dto)
                .map(pair -> {
                    var follow = new Follow();
                    follow.setListener(pair.listener());
                    follow.setArtist(pair.artist());
                    return followRepository.save(follow);
                })
                .orElseThrow(() -> new IllegalArgumentException("Listener or artist not found"));
    }

    public boolean isFollowing(int listenerId, int artistId) {
        return followRepository.existsByListenerListenerIdAndArtistArtistId(listenerId, artistId);
    }

    public Follow updateFollow(Integer id, FollowDTO dto) {
        return followRepository.findById(id)
                .flatMap(existing ->
                        getListenerAndArtist(dto)
                                .map(pair -> {
                                    existing.setListener(pair.listener());
                                    existing.setArtist(pair.artist());
                                    return followRepository.save(existing);
                                })
                )
                .orElseThrow(() -> new IllegalArgumentException("Follow, listener, or artist not found"));
    }

    public void deleteFollow(FollowDTO dto) {
        getListenerAndArtist(dto).ifPresentOrElse(
                pair -> followRepository.deleteByListenerAndArtist(pair.listener(), pair.artist()),
                () -> {
                    throw new EntityNotFoundException("Listener or artist not found with provided IDs");
                }
        );
    }

    private Optional<Pair> getListenerAndArtist(FollowDTO dto) {
        return listenerRepository.findById(dto.getListenerId())
                .flatMap(listener -> artistRepository.findById(dto.getArtistId())
                        .map(artist -> new Pair(listener, artist)));
    }

    private record Pair(Listener listener, Artist artist) {
    }
}
