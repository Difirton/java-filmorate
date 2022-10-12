package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFilmMark;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class UserFilmMarkMapper implements RowMapper<UserFilmMark> {
    @Override
    public UserFilmMark mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFilmMark.builder()
                .id(rs.getLong(1))
                .user(User.builder()
                        .id(rs.getLong(2))
                        .name(rs.getString(3))
                        .email(rs.getString(4))
                        .login(rs.getString(5))
                        .birthday(this.getDate(rs, 6))
                        .build())
                .film(Film.builder()
                        .id(rs.getLong(7))
                        .name(rs.getString(8))
                        .description(rs.getString(9))
                        .duration(rs.getInt(10))
                        .releaseDate(this.getDate(rs, 11))
                        .rate(rs.getDouble(12))
                        .build())
                .mark(rs.getInt(13))
                .build();
    }

    private LocalDate getDate(ResultSet rs, int field) throws SQLException {
        Date dateToCheck = rs.getDate(field);
        if (dateToCheck == null) {
            return null;
        }
        return dateToCheck.toLocalDate();
    }
}
