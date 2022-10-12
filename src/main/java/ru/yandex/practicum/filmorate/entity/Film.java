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
@EqualsAndHashCode(exclude = {"usersLikes", "genres", "ratingMPA", "directors"})
@ToString(exclude = {"usersLikes", "genres", "ratingMPA", "directors"})
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
    private Integer rate = 0;

    @NotNull
    @JsonProperty(value = "mpa")
    private RatingMPA ratingMPA;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<User> usersLikes = new ArrayList<>();

    private List<Genre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();

    public static FilmBuilder builder() {
        return new FilmBuilder();
    }

    public void addUserLike(User user) {
        this.rate++;
        usersLikes.add(user);
        user.getLikesFilms().add(this);
    }

    public void removeUserLike(User user) {
        this.rate--;
        usersLikes.remove(user);
        user.getLikesFilms().remove(this);
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
        private Integer rate = 0;

        @NotNull
        private RatingMPA ratingMPA;

        private List<User> usersLikes = new ArrayList<>();

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

        public FilmBuilder rate(Integer rate) {
            this.rate = rate;
            return this;
        }

        public FilmBuilder ratingMPA(RatingMPA ratingMPA) {
            this.ratingMPA = ratingMPA;
            return this;
        }

        public FilmBuilder usersLikes(List<User> usersLikes) {
            this.usersLikes = usersLikes;
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
            return new Film(id, name, description, releaseDate, duration, rate, ratingMPA, usersLikes, genres, directors);
        }
    }
}
