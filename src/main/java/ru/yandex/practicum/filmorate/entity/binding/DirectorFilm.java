package ru.yandex.practicum.filmorate.entity.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectorFilm {
    private Long directorId;

    private Long filmId;

    public static DirectorFilmBuilder builder() {
        return new DirectorFilmBuilder();
    }

    public static class DirectorFilmBuilder {
        private Long directorId;

        private Long filmId;

        private DirectorFilmBuilder() { }

        public DirectorFilmBuilder directorId(Long directorId) {
            this.directorId = directorId;
            return this;
        }

        public DirectorFilmBuilder filmId(Long filmId) {
            this.filmId = filmId;
            return this;
        }

        public DirectorFilm build() {
            return new DirectorFilm(directorId, filmId);
        }
    }
}
