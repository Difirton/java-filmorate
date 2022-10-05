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
        filmService.createFilm(Film.builder().id(1L).name("name film 1").description("description film 1")
                                   .releaseDate(LocalDate.of(1967, 3, 25)).duration(100)
                                   .ratingMPA(RatingMPA.builder().id(1L).title("G").build()).build());
        filmService.createFilm(Film.builder().id(2L).name("name film 2").description("description film 2")
                                   .releaseDate(LocalDate.of(1997, 5, 1)).duration(300)
                                   .ratingMPA(RatingMPA.builder().id(1L).title("G").build()).build());
        filmService.createFilm(Film.builder().id(3L).name("name film 3").description("description film 3")
                                   .releaseDate(LocalDate.of(2007, 10, 12)).duration(200)
                                   .ratingMPA(RatingMPA.builder().id(2L).title("PG").build()).build());

        userService.createUser(User.builder().id(1L).email("example1@gmail.ru").login("231ffdsf32").name("Test1 Test1")
                                   .birthday(LocalDate.of(1990, 5, 1)).build());
        userService.createUser(User.builder().id(2L).email("mail2@mail.ru").login("FFad23fdas").name("Test2 Tes2")
                                   .birthday(LocalDate.of(1981, 11, 15)).build());
        userService.createUser(User.builder().id(3L).email("example3@mail.com").login("648448fafaf22").name("Test3 Tes3")
                                   .birthday(LocalDate.of(1995, 8, 20)).build());
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
