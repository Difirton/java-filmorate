package ru.yandex.practicum.filmorate.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
@ToString(exclude = "films")
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email should not be empty")
    @NotBlank(message = "Email should not be blank")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Login should not be empty")
    @NotBlank(message = "Login should not be blank")
    @Pattern(regexp = "/^\\S+$/", message = "Login should not contain spaces")
    @Column(unique = true, nullable = false)
    private String login;

    private String name = login;

    @Past
    private LocalDate birthday;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "users_films",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> films = new ArrayList<>();
}
