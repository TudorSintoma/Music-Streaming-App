package com.tudor.demo.repository;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Integer>, JpaSpecificationExecutor<Artist> {
    Optional<Artist> findByUser(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Artist a SET a.genreId = :genreId WHERE a.user.userId = :userId")
    void updateGenre(int userId, int genreId);

    List<Artist> findByGenreId(Integer genreId);
}
