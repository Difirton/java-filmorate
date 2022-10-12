package ru.yandex.practicum.filmorate.entity.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilmMark {
    Long userId;
    Long filmId;
    Integer mark;

    public static UserFilmMarkBuilder builder() {
        return new UserFilmMarkBuilder();
    }

    public static class UserFilmMarkBuilder {
        private Long userId;
        private Long filmId;
        private Integer mark;

        private UserFilmMarkBuilder() {
        }

        public UserFilmMarkBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserFilmMarkBuilder filmId(Long filmId) {
            this.filmId = filmId;
            return this;
        }

        public UserFilmMarkBuilder mark(Integer mark) {
            this.mark = mark;
            return this;
        }

        public UserFilmMark build() {
            return new UserFilmMark(userId, filmId, mark);
        }
    }
}
