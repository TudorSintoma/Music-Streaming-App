package com.tudor.demo.model;

import java.util.HashMap;
import java.util.Map;

public enum Genre {
    ROCK(1),
    POP(2),
    JAZZ(3),
    HIPHOP(4),
    CLASSICAL(5),
    EDM(6);

    private final int id;

    private static final Map<Integer, Genre> map = new HashMap<>();

    static {
        for (Genre genre : Genre.values()) {
            map.put(genre.id, genre);
        }
    }

    Genre(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Genre fromId(int id) {
        return map.get(id);
    }
}

