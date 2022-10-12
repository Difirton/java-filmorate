package ru.yandex.practicum.filmorate.entity.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmGenre {
    private Long filmId;
    private Long genreId;

    public static FilmGenreBuilder builder() {
        return new FilmGenreBuilder();
    }

    public static class FilmGenreBuilder {
        private Long filmId;
        private Long genreId;

        private FilmGenreBuilder() { }

        public FilmGenreBuilder filmId(Long filmId) {
            this.filmId = filmId;
            return this;
        }

        public FilmGenreBuilder genreId(Long genreId) {
            this.genreId = genreId;
            return this;
        }

        public FilmGenre build() {
            return new FilmGenre(filmId, genreId);
        }
    }
}
