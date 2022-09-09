package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRepositoryMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .build();
    }
}
