package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectorFilm {
    private Long directorId;
    private Long filmId;
}
