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
@Builder
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

    @Builder.Default
    @PositiveOrZero
    private Integer rate = 0;

    @NotNull
    @JsonProperty(value = "mpa")
    private RatingMPA ratingMPA;

    @Builder.Default
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<User> usersLikes = new ArrayList<>();

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<Director> directors = new ArrayList<>();

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
}
