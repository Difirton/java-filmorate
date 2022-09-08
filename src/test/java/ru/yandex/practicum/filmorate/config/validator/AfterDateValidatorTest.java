package ru.yandex.practicum.filmorate.config.validator;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AfterDateValidatorTest {
    private final Validator validator;
    @Autowired
    private AfterDateValidatorTest(Validator validator) {
        this.validator = validator;
    }

    @Test
    @DisplayName("Test custom validation with valid date: after 28.12.1895")
    void testWithValidDate() {
        Film film = Film.builder()
                .id(1L)
                .name("name film 1")
                .description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test custom validation with not valid date: before 28.12.1895")
    void testWithNotValidDate() {
        Film film = Film.builder()
                .id(1L)
                .name("name film 1")
                .description("description film 1")
                .releaseDate(LocalDate.of(1267, 3, 25))
                .duration(100)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Release date should be not earlier than december 28, 1895",
                violations.iterator().next().getMessage());
    }
}