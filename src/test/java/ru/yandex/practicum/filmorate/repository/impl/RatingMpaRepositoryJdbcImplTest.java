package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RatingMpaRepositoryJdbcImplTest {
    private RatingMPA newRating;
    private RatingMPA secondNewRating;
    private RatingMPA thirdNewRating;
    @Autowired
    private RatingMpaRepository ratingMpaRepository;

    @BeforeEach
    void setUp() {
        newRating = RatingMPA.builder().title("newR").build();
        secondNewRating = RatingMPA.builder().title("secR").build();
        thirdNewRating = RatingMPA.builder().title("third").build();
    }

    @Test
    @DisplayName("Test save in RatingMpaRepository")
    void testSave() {
        RatingMPA returnedMPA = ratingMpaRepository.save(newRating);
        assertEquals(6, returnedMPA.getId());
        assertEquals("newR", returnedMPA.getTitle());
        RatingMPA ratingAfterSaveInDB = ratingMpaRepository.findById(6L).get();
        assertEquals("newR", ratingAfterSaveInDB.getTitle());
    }

    @Test
    @DisplayName("Test update in RatingMpaRepository")
    void testUpdate() {
        newRating.setId(1L);
        RatingMPA returnedMPA = ratingMpaRepository.update(newRating);
        assertEquals("newR", returnedMPA.getTitle());
        RatingMPA ratingAfterSaveInDB = ratingMpaRepository.findById(1L).get();
        assertEquals("newR", ratingAfterSaveInDB.getTitle());
    }

    @Test
    @DisplayName("Test delete by id in RatingMpaRepository")
    void testDeleteById() {
        ratingMpaRepository.deleteById(1L);
        assertEquals(4, ratingMpaRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all in RatingMpaRepository")
    void testFindAll() {
        assertEquals(5, ratingMpaRepository.findAll().size());
        ratingMpaRepository.save(newRating);
        assertEquals(6, ratingMpaRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find by id in RatingMpaRepository")
    void testFindById() {
        assertEquals("G", ratingMpaRepository.findById(1L).get().getTitle());
        assertEquals("PG-13", ratingMpaRepository.findById(3L).get().getTitle());
    }

    @Test
    @DisplayName("Test save List ratings in RatingMpaRepository")
    void testSaveAll() {
        ratingMpaRepository.saveAll(List.of(newRating, secondNewRating, thirdNewRating));
        assertEquals(8, ratingMpaRepository.findAll().size());
        assertEquals("newR", ratingMpaRepository.findById(6L).get().getTitle());
        assertEquals("secR", ratingMpaRepository.findById(7L).get().getTitle());
        assertEquals("third", ratingMpaRepository.findById(8L).get().getTitle());
    }
}