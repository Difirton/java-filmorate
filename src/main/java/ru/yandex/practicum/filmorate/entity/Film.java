package ru.yandex.practicum.filmorate.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.config.validator.AfterDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "usersLikes")
@ToString(exclude = "usersLikes")
@Builder
@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Film name should not be empty")
    @NotBlank(message = "Film name should not be empty")
    private String name;

    @Length(max = 200, message = "Should be less than 200 characters")
    @Column(length = 200)
    private String description;

    @AfterDate(day = 28, month = 12, year = 1895, message = "Release date should be not earlier than december 28, 1895")
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @PositiveOrZero
    private Integer duration;

    @Builder.Default
    @PositiveOrZero
    private Integer rate = 0;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "users_likes_films",
            joinColumns = @JoinColumn(name = "film_id",
                    foreignKey = @ForeignKey(name = "FK_FILM_ID"),
                    nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id",
                    foreignKey = @ForeignKey(name = "FK_USER_ID"),
                    nullable = false)
    )
    @Column(name = "user_likes")
    private List<User> usersLikes = new ArrayList<>();

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
