package ru.yandex.practicum.filmorate.entity.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmGenre {
    private Long filmId;
    private Long genreId;
}
