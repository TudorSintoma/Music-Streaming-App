package com.tudor.demo.specification;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.Genre;
import com.tudor.demo.model.User;
import org.springframework.data.jpa.domain.Specification;

public class ArtistSpecifications {

    public static Specification<Artist> hasGenreId(Integer genreId) {
        return (root, query, cb) ->
                genreId == null ? null : cb.equal(root.get("genreId"), genreId);
    }



    public static Specification<Artist> hasPopularity(String popularity) {
        return (root, query, cb) ->
                popularity == null ? null : cb.equal(root.get("popularity"), popularity);
    }

    public static Specification<Artist> hasDebutYearInCategory(String category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            switch (category) {
                case "BEFORE_1950":
                    return cb.lessThan(root.get("year"), 1950);
                case "1950_1970":
                    return cb.between(root.get("year"), 1950, 1970);
                case "1970_1990":
                    return cb.between(root.get("year"), 1970, 1990);
                case "1990_2010":
                    return cb.between(root.get("year"), 1990, 2010);
                case "AFTER_2010":
                    return cb.greaterThanOrEqualTo(root.get("year"), 2010);
                default:
                    return null;
            }
        };
    }


    public static Specification<Artist> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("user").get("fullName")), "%" + name.toLowerCase() + "%");
    }
}
