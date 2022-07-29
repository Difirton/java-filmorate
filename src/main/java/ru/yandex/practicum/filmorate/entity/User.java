package ru.yandex.practicum.filmorate.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")
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
}
