package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;


import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationRepositoryTest {
    private final RecommendationRepository recommendationRepository;
    private final FilmService filmService;
    private final UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findRecommendationsByUserTest() {
        //      matrix of likes
        //
        //      USER_ID |   1   2   3
        //      FILM_ID--------------
        //         1    |   1	1	0
        //         2    |   1	1	0
        //         3    |   1	0	1

        filmService.addLikeFilm(1L, 1L);
        filmService.addLikeFilm(1L, 2L);
        filmService.addLikeFilm(2L, 1L);
        filmService.addLikeFilm(2L, 2L);
        filmService.addLikeFilm(3L, 1L);
        filmService.addLikeFilm(3L, 3L);

        Assertions.assertThat(recommendationRepository.findRecommendationsByUser(2L))
                  .isEqualTo(List.of(3L));
        Assertions.assertThat(recommendationRepository.findRecommendationsByUser(3L))
                  .isEqualTo(List.of(1L, 2L));
    }
}
