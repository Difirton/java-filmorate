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
            joinColumns = {@JoinColumn(name = "addUser", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "friends", referencedColumnName = "id", nullable = false)}
    )
    private Set<User> addUser = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "addUser", cascade = CascadeType.ALL)
    private Set<User> friends = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "users_likes_films",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> likesFilms = new ArrayList<>();
}
