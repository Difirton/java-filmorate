package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRepositoryEagerMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(this.getDate(rs))
                .duration(rs.getInt("duration"))
                .rate(rs.getDouble("rate"))
                .ratingMPA(RatingMPA.builder()
                        .id(rs.getLong("rating_mpa_id"))
                        .title(rs.getString("title"))
                        .build())
                .build();
    }

    private LocalDate getDate(ResultSet rs) throws SQLException {
        Date dateToCheck = rs.getDate("release_date");
        if (dateToCheck == null) {
            return null;
        }
        return dateToCheck.toLocalDate();
    }
}
