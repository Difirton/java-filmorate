package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
@ToString(exclude = {"likesFilms", "users", "friends"})
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
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JoinTable(
            name = "user_friends",
            uniqueConstraints = @UniqueConstraint(name = "UNQ_USER_FRIEND", columnNames = {"USER_ID", "FRIEND_ID"}),
            joinColumns = {@JoinColumn(name = "friend_id",
                    foreignKey = @ForeignKey(name = "FK_FRIEND_ID"),
                    referencedColumnName = "id",
                    nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "user_id",
                    foreignKey = @ForeignKey(name = "FK_USER_ID"),
                    referencedColumnName = "id",
                    nullable = false)}
    )
    private List<User> friends = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @ManyToMany(mappedBy = "friends", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "usersLikes")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
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
