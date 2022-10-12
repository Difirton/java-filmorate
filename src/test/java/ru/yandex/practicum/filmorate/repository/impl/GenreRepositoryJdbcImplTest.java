package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GenreRepositoryJdbcImplTest {
    private Genre newGenre;
    private Genre secondGenre;
    private Genre thirdGenre;
    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        newGenre = Genre.builder().title("newGenre").build();
        secondGenre = Genre.builder().title("secondGenre").build();
        thirdGenre = Genre.builder().title("thirdGenre").build();
    }

    @Test
    @DisplayName("Test save in GenreRepository")
    void testSave() {
        Genre returnedGenre = genreRepository.save(newGenre);
        assertEquals(7, newGenre.getId());
        assertEquals("newGenre", returnedGenre.getTitle());
        Genre genreAfterSaveInDB = genreRepository.findById(7L).get();
        assertEquals("newGenre", genreAfterSaveInDB.getTitle());
    }

    @Test
    @DisplayName("Test update in GenreRepository")
    void testUpdate() {
        newGenre.setId(1L);
        Genre returnedGenre = genreRepository.update(newGenre);
        assertEquals("newGenre", returnedGenre.getTitle());
        Genre genreAfterSaveInDB = genreRepository.findById(1L).get();
        assertEquals("newGenre", genreAfterSaveInDB.getTitle());
    }

    @Test
    @DisplayName("Test delete by id in GenreRepository")
    void testDeleteById() {
        genreRepository.deleteById(1L);
        assertEquals(5, genreRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all in GenreRepository")
    void testFindAll() {
        assertEquals(6, genreRepository.findAll().size());
        genreRepository.save(newGenre);
        assertEquals(7, genreRepository.findAll().size());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_findGenresByFilmId.sql"})
    @DisplayName("Test find by id in GenreRepository")
    void testFindById() {
        assertEquals("test", genreRepository.findById(7L).get().getTitle());
    }

    @Test
    @DisplayName("Test save List ratings in GenreRepository")
    void testSaveAll() {
        genreRepository.saveAll(List.of(newGenre, secondGenre, thirdGenre));
        assertEquals(9, genreRepository.findAll().size());
        assertEquals("newGenre", genreRepository.findById(7L).get().getTitle());
        assertEquals("secondGenre", genreRepository.findById(8L).get().getTitle());
        assertEquals("thirdGenre", genreRepository.findById(9L).get().getTitle());
    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_findGenresByFilmId.sql"})
    @DisplayName("Test find genres by film id in GenreRepository")
    void testFindGenresByFilmId() {
        List<Genre> genres = genreRepository.findGenresByFilmId(1L);
        assertEquals("test", genres.get(0).getTitle());
        assertEquals("test_2", genres.get(1).getTitle());
    }
}