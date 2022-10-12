package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.config.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"usersMarks", "genres", "ratingMPA", "directors"})
@ToString(exclude = {"usersMarks", "genres", "ratingMPA", "directors"})
public class Film {
    private Long id;

    @NotNull(message = "Film name should not be empty")
    @NotBlank(message = "Film name should not be empty")
    private String name;

    @Length(max = 200, message = "Should be less than 200 characters")
    private String description;

    @AfterDate(day = 28, month = 12, year = 1895, message = "Release date should be not earlier than december 28, 1895")
    private LocalDate releaseDate;

    @PositiveOrZero
    private Integer duration;

    @PositiveOrZero
    private Double rate = 0.0;

    @NotNull
    @JsonProperty(value = "mpa")
    private RatingMPA ratingMPA;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<UserFilmMark> usersMarks = new ArrayList<>();

    private List<Genre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();

    public static FilmBuilder builder() {
        return new FilmBuilder();
    }

    public void addUserMark(UserFilmMark userFilmMark) {
        usersMarks.add(userFilmMark);
        userFilmMark.getUser().getMarksFilms().add(userFilmMark);
    }

    public void removeUserMark(UserFilmMark userFilmMark) {
        usersMarks.remove(userFilmMark);
        userFilmMark.getUser().getMarksFilms().remove(userFilmMark);
    }

    public static class FilmBuilder {
        private Long id;

        @NotNull(message = "Film name should not be empty")
        @NotBlank(message = "Film name should not be empty")
        private String name;

        @Length(max = 200, message = "Should be less than 200 characters")
        private String description;

        private LocalDate releaseDate;

        @PositiveOrZero
        private Integer duration;

        @PositiveOrZero
        private Double rate = 0.0;

        @NotNull
        private RatingMPA ratingMPA;

        private List<UserFilmMark> usersMarks = new ArrayList<>();

        private List<Genre> genres = new ArrayList<>();

        private List<Director> directors = new ArrayList<>();

        private FilmBuilder() { }

        public FilmBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FilmBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FilmBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FilmBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public FilmBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public FilmBuilder rate(Double rate) {
            this.rate = rate;
            return this;
        }

        public FilmBuilder ratingMPA(RatingMPA ratingMPA) {
            this.ratingMPA = ratingMPA;
            return this;
        }

        public FilmBuilder usersMarks(List<UserFilmMark> usersMarks) {
            this.usersMarks = usersMarks;
            return this;
        }

        public FilmBuilder genres(List<Genre> genres) {
            this.genres = genres;
            return this;
        }

        public FilmBuilder directors(List<Director> directors) {
            this.directors = directors;
            return this;
        }

        public Film build() {
            return new Film(id, name, description, releaseDate, duration, rate, ratingMPA, usersMarks, genres, directors);
        }
    }
}
