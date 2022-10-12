package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmRepositoryJdbcImplTest {
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
        film1.setRate(10.0);
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmRepository.update(film1);
        assertEquals("updated", filmRepository.findById(1L).get().getName());
        assertEquals("updated", filmRepository.findById(1L).get().getDescription());
        assertEquals(1000, filmRepository.findById(1L).get().getDuration());
        assertEquals("R", filmRepository.findById(1L).get().getRatingMPA().getTitle());
        assertEquals(10, filmRepository.findById(1L).get().getRate());
        assertEquals(LocalDate.of(2000, 1, 1), filmRepository.findById(1L).get().getReleaseDate());
    }

    @Test
    @DisplayName("Test delete by id film in FilmRepository")
    void testDeleteById() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(3, filmRepository.findAll().size());
        filmRepository.deleteById(1L);
        assertEquals(2, filmRepository.findAll().size());
        filmRepository.deleteById(2L);
        assertEquals(1, filmRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all films in FilmRepository")
    void testFindAll() {
        filmRepository.save(film1);
        filmRepository.save(film2);
        filmRepository.save(film3);
        assertEquals(List.of(film1, film2, film3), filmRepository.findAll());
    }

    @Test
    @DisplayName("Test find film by id in FilmRepository")
    void testFindById() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(film2, filmRepository.findById(2L).get());
    }

    @Test
    @DisplayName("Test find popular films in FilmRepository")
    void testFindPopularFilmsByRate() {
        assertEquals(List.of(), filmRepository.findPopularFilmsByRate(10));
        film1.setRate(5.0);
        film2.setRate(3.0);
        film3.setRate(10.0);
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(List.of(film3, film1, film2), filmRepository.findPopularFilmsByRate(10));
    }

    @Test
    @DisplayName("Test size of list find popular films in FilmRepository")
    void testFindPopularFilmsByRateAndSize() {
        assertEquals(List.of(), filmRepository.findPopularFilmsByRate(10));
        film1.setRate(3.0);
        film2.setRate(2.0);
        film3.setRate(1.0);
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(List.of(film1, film2, film3), filmRepository.findPopularFilmsByRate(10));
        assertEquals(List.of(film1, film2), filmRepository.findPopularFilmsByRate(2));
    }

    @Test
    @DisplayName("Test save all list of films in FilmRepository")
    void testSaveAll() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals(List.of(film1, film2, film3), filmRepository.findAll());
    }

    @Test
    @DisplayName("Test update all list of films in FilmRepository")
    void testUpdateAll() {
        filmRepository.saveAll(List.of(film1, film2, film3));
        film1.setName("updated");
        film2.setDescription("updated");
        film3.setRate(11.0);
        film1.setDuration(1000);
        film3.setRatingMPA(RatingMPA.builder().id(4L).build());
        filmRepository.updateAll(List.of(film1, film2, film3));
        assertEquals(film1, filmRepository.findById(1L).get());
        assertEquals(film2, filmRepository.findById(2L).get());
        assertEquals(film3, filmRepository.findById(3L).get());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql","classpath:sql_scripts/schema_saveFilmsWithDirector.sql"})
    @DisplayName("Test film with director")
    void testSaveFilmWithDirector() {
        film1.setDirectors(List.of(Director.builder()
                .id(1L)
                .build()));
        filmRepository.save(film1);
        assertEquals("director 1", filmRepository.findById(1L).get().getDirectors().get(0).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_saveFilmsWithDirector.sql"})
    @DisplayName("Test film with two directors")
    void testSaveFilmWithTwoDirectors() {
        film1.setDirectors(List.of(Director.builder()
                .id(1L)
                .build(),
                Director.builder()
                .id(2L)
                .build()));
        film3.setDirectors(List.of(Director.builder()
                        .id(2L)
                        .build()));
        filmRepository.saveAll(List.of(film1, film2, film3));
        assertEquals("director 1", filmRepository.findById(1L).get().getDirectors().get(0).getName());
        assertEquals("director 2", filmRepository.findById(1L).get().getDirectors().get(1).getName());
        assertEquals("director 2", filmRepository.findById(3L).get().getDirectors().get(0).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test find list of films by director id without parameters of sort")
    void testFindFilmsByDirectorId() {
        List<Film> filmsDirector1 = filmRepository.findFilmsByDirectorId(1L);
        List<Film> filmsDirector2 = filmRepository.findFilmsByDirectorId(2L);
        assertEquals("test name 1", filmsDirector1.get(0).getName());
        assertEquals("test name 1", filmsDirector2.get(0).getName());
        assertEquals("test name 2", filmsDirector1.get(1).getName());
        assertEquals("test name 3", filmsDirector2.get(1).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql","classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test search for list of films by director id sorted by year")
    void testFindFilmsByDirectorIdWithParamYear() {
        List<Film> sortedFilmsDirector1 = filmRepository.findFilmsByDirectorId(1L, "year");
        List<Film> sortedFilmsDirector2 = filmRepository.findFilmsByDirectorId(2L, "year");
        assertEquals("test name 1", sortedFilmsDirector1.get(0).getName());
        assertEquals("test name 2", sortedFilmsDirector1.get(1).getName());
        assertEquals("test name 1", sortedFilmsDirector2.get(0).getName());
        assertEquals("test name 3", sortedFilmsDirector2.get(1).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test search for list of films by director id sorted by likes")
    void testFindFilmsByDirectorIdWithParamLikes() {
        List<Film> sortedFilmsDirector1 = filmRepository.findFilmsByDirectorId(1L, "likes");
        List<Film> sortedFilmsDirector2 = filmRepository.findFilmsByDirectorId(2L, "likes");
        assertEquals("test name 2", sortedFilmsDirector1.get(0).getName());
        assertEquals("test name 1", sortedFilmsDirector1.get(1).getName());
        assertEquals("test name 1", sortedFilmsDirector2.get(0).getName());
        assertEquals("test name 3", sortedFilmsDirector2.get(1).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test search for list of films by director id sorted by likes")
    void testFindFilmsByIdsTwoElements() {
        List<Long> testData = List.of(2L, 3L);
        List<Film> result = filmRepository.findFilmsByIds(testData);
        assertEquals("test name 2", result.get(0).getName());
        assertEquals("test name 3", result.get(1).getName());
        assertEquals(2, result.size());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql","classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test search for list of films by director id sorted by likes")
    void testFindFilmsByIdsEmptyList() {
        List<Long> testData = List.of();
        List<Film> result = filmRepository.findFilmsByIds(testData);
        assertTrue(result.isEmpty());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql","classpath:sql_scripts/schema_findFilmsByDirectorId.sql"})
    @DisplayName("Test search for list of films by director id sorted by likes")
    void testFindFilmsByIdsOneElement() {
        List<Long> testData = List.of(1L);
        List<Film> result = filmRepository.findFilmsByIds(testData);
        assertEquals("test name 1", result.get(0).getName());
        assertEquals(1, result.size());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_searchFilms.sql"})
    @DisplayName("Test search films by director name sorted by likes")
    void testSearchFilmsByDirectorName() {
        List<Film> films1 = filmRepository.searchFilmsByDirectorName("hitton");
        List<Film> films2 = filmRepository.searchFilmsByDirectorName("k");
        assertEquals(1, films1.size());
        assertEquals(3, films2.size());
        assertEquals(20, films1.get(0).getRate());
        assertEquals("Toy`s history 2", films1.get(0).getName());
        assertEquals("Toy`s history 1", films2.get(0).getName());
        assertEquals("Toy`s history 2", films2.get(1).getName());
        assertEquals("Lucky man", films2.get(2).getName());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_searchFilms.sql"})
    @DisplayName("Test search films by film name sorted by likes")
    void testSearchFilmsByName() {
        List<Film> films = filmRepository.searchFilmsByName("toy");
        assertEquals("Toy`s history 1", films.get(0).getName());
        assertEquals("Toy`s history 2", films.get(1).getName());
    }
}