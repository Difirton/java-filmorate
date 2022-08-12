package ru.yandex.practicum.filmorate.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
@ToString(exclude = {"films", "addUser", "friends"})
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email should not be blank")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Login should not be blank")
    @Pattern(regexp = "[a-zA-Z0-9_.]*", message = "Login should not contain spaces")
    @Column(unique = true, nullable = false)
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "add_user", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "friends", referencedColumnName = "id", nullable = false)}
    )
    @Column(name = "add_user")
    private List<User> addUser = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "addUser", cascade = CascadeType.ALL)
    private List<User> friends = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "users_likes_films",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    @Column(name = "likes_films")
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
        friends.add(user);
        user.getFriends().add(this);
    }

    public void removeFriend(User user) {
        friends.remove(user);
        user.getFriends().remove(this);
    }
}
