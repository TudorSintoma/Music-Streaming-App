package com.tudor.demo.repository;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.Follow;
import com.tudor.demo.model.Listener;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    boolean existsByListenerListenerIdAndArtistArtistId(int listenerId, int artistId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.artist = :artist AND f.listener = :listener")
    void deleteByListenerAndArtist(@Param("listener") Listener listener, @Param("artist") Artist artist);

    List<Follow> findByListenerListenerId(Integer listenerId);
}
