package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFilmMark;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserFilmMarkMapper implements RowMapper<UserFilmMark> {
    @Override
    public UserFilmMark mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFilmMark.builder()
                .id(rs.getLong("ufm.id"))
                .user(User.builder()
                        .id(rs.getLong("ufm.user_id"))
                        .name(rs.getString("u.name"))
                        .email(rs.getString("u.email"))
                        .login(rs.getString("u.login"))
                        .birthday(this.getDate(rs, "u.birthday"))
                        .build())
                .film(Film.builder()
                        .id(rs.getLong("ufm.film_id"))
                        .name(rs.getString("f.name"))
                        .duration(rs.getInt("f.description"))
                        .releaseDate(this.getDate(rs, "f.release_date"))
                        .rate(rs.getDouble("f.rate"))
                        .build())
                .mark(rs.getInt("ufm.mark"))
                .build();
    }

    private LocalDate getDate(ResultSet rs, String fieldName) throws SQLException {
        Date dateToCheck = rs.getDate(fieldName);
        if (dateToCheck == null) {
            return null;
        }
        return dateToCheck.toLocalDate();
    }
}
