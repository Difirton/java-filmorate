package ru.yandex.practicum.filmorate.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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

    @NotBlank
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "/^\\S*$/", message = "Login should not contain spaces")
    private String login;

    private String name = login;

    @Past
    private LocalDate birthday;

}
