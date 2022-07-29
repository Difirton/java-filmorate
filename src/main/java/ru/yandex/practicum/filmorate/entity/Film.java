package ru.yandex.practicum.filmorate.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.config.validator.AfterDate;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Film name should not be empty")
    private String name;

    @Length(max = 200, message = "Should be less than 200 characters")
    @Column(length = 200)
    private String description;

    @AfterDate(day = 28, month = 12, year = 1985, message = "Release date should be no earlier than december 28, 1895")
    @FutureOrPresent
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Positive(message = "Should be positive")
    private Duration duration;
}
