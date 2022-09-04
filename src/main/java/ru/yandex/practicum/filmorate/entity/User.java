package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
@ToString(exclude = {"likesFilms", "friends"})
@Builder
public class User {
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @NotBlank(message = "Login should not be blank")
    @Pattern(regexp = "[a-zA-Z0-9_.]*", message = "Login should not contain spaces")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    @Builder.Default
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<UserFriend> friends = new ArrayList<>();

    @Builder.Default
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<Film> likesFilms = new ArrayList<>();

    public void addLikeFilm(Film film) {
        likesFilms.add(film);
        film.getUsersLikes().add(this);
    }

    public void removeLikeFilm(Film film) {
        likesFilms.remove(film);
        film.getUsersLikes().remove(this);
    }

    public void addFriend(User user) {
        UserFriend newFriend = UserFriend.builder()
                .user(this)
                .friend(user)
                .approved(true).build();
        friends.add(newFriend);
    }

    public void removeFriend(User user) {
        UserFriend removedFriend = UserFriend.builder()
                .user(this)
                .friend(user).build();
        friends.remove(removedFriend);
    }
}
