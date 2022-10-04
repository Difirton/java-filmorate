package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JdbcFilmRepositoryImplTest {
    private Film film1;
    private Film film2;
    private Film film3;
    @Autowired
    private FilmRepository filmRepository;

    @BeforeEach
    void setUp() {
        film1 = Film.builder().id(1L).name("name film 1").description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25)).duration(100)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build()).build();
        film2 = Film.builder().id(2L).name("name film 2").description("description film 2")
                .releaseDate(LocalDate.of(1997, 5, 1)).duration(300)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build()).build();
        film3 = Film.builder().id(3L).name("name film 3").description("description film 3")
                .releaseDate(LocalDate.of(2007, 10, 12)).duration(200)
                .ratingMPA(RatingMPA.builder().id(1L).title("PG-13").build()).build();
    }

    @Test
    @DisplayName("Test save film in FilmRepository")
    void testSave() {
        filmRepository.save(film1);
        assertEquals(filmRepository.findById(1L).get(), film1);
    }

    @Test
    @DisplayName("Test update film in FilmRepository")
    void testUpdate() {
        filmRepository.save(film1);
        film1.setName("updated");
        film1.setDescription("updated");
        film1.setDuration(1000);
        film1.setRatingMPA(RatingMPA.builder().id(4L).build());
        film1.setRate(10);
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmRepository.update(film1);
        assertEquals(filmRepository.findById(1L).get().getName(), "updated");
        assertEquals(filmRepository.findById(1L).get().getDescription(), "updated");
        assertEquals(filmRepository.findById(1L).get().getDuration(), 1000);
        assertEquals(filmRepository.findById(1L).get().getRatingMPA().getTitle(), "R");
        assertEquals(filmRepository.findById(1L).get().getRate(), 10);
        assertEquals(filmRepository.findById(1L).get().getReleaseDate(), LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Test delete by id film in FilmRepository")
    void testDeleteById() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(filmRepository.findAll().size(), 3);
        filmRepository.deleteById(1L);
        assertEquals(filmRepository.findAll().size(), 2);
        filmRepository.deleteById(2L);
        assertEquals(filmRepository.findAll().size(), 1);
    }

    @Test
    @DisplayName("Test find all films in FilmRepository")
    void testFindAll() {
        filmRepository.save(film1);
        filmRepository.save(film2);
        filmRepository.save(film3);
        assertEquals(filmRepository.findAll(), List.of(film1, film2, film3));
    }

    @Test
    @DisplayName("Test find film by id in FilmRepository")
    void testFindById() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(filmRepository.findById(2L).get(), film2);
    }

    @Test
    @DisplayName("Test find popular films in FilmRepository")
    void testFindPopularFilmsByRate() {
        assertEquals(filmRepository.findPopularFilmsByRate(10), List.of());
        film1.setRate(5);
        film2.setRate(3);
        film3.setRate(10);
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(filmRepository.findPopularFilmsByRate(10), List.of(film3, film1, film2));
    }

    @Test
    @DisplayName("Test size of list find popular films in FilmRepository")
    void testFindPopularFilmsByRateAndSize() {
        assertEquals(filmRepository.findPopularFilmsByRate(10), List.of());
        film1.setRate(3);
        film2.setRate(2);
        film3.setRate(1);
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(filmRepository.findPopularFilmsByRate(10), List.of(film1, film2, film3));
        assertEquals(filmRepository.findPopularFilmsByRate(2), List.of(film1, film2));
    }

    @Test
    @DisplayName("Test save all list of films in FilmRepository")
    void testSaveAll() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(filmRepository.findAll(), List.of(film1, film2, film3));
    }

    @Test
    @DisplayName("Test update all list of films in FilmRepository")
    void testUpdateAll() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        film1.setName("updated");
        film2.setDescription("updated");
        film3.setRate(11);
        film1.setDuration(1000);
        film2.setReleaseDate(LocalDate.of(1900, 1, 1));
        film3.setRatingMPA(RatingMPA.builder().id(4L).build());
        filmRepository.updateAll(List.of(film1, film2, film3));
    }
}