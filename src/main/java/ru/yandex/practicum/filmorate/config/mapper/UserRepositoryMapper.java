package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class UserRepositoryMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(this.getDate(rs))
                .build();
    }

    private LocalDate getDate(ResultSet rs) throws SQLException {
        Date dateToCheck = rs.getDate("birthday");
        if (dateToCheck == null) {
            return null;
        }
        return dateToCheck.toLocalDate();
    }
}
