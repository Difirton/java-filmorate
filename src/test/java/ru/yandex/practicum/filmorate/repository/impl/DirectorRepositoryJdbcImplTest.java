package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DirectorRepositoryJdbcImplTest {

    private Director newDirector;
    private Director secondDirector;
    private Director thirdDirector;

    @Autowired
    private DirectorRepository directorRepository;

    @BeforeEach
    void setUp() {
        newDirector = Director.builder().name("newDirector").build();
        secondDirector = Director.builder().name("secondDirector").build();
        thirdDirector = Director.builder().name("thirdDirector").build();
    }

    @Test
    @DisplayName("Test save in DirectorRepository")
    void testSave() {
        Director returnedDirector = directorRepository.save(newDirector);
        assertEquals(1, newDirector.getId());
        assertEquals("newDirector", returnedDirector.getName());
        Director directorAfterSaveInDB = directorRepository.findById(1L).get();
        assertEquals("newDirector", directorAfterSaveInDB.getName());
    }

    @Test
    @DisplayName("Test update in DirectorRepository")
    void testUpdate() {
        directorRepository.save(newDirector);
        newDirector.setId(1L);
        Director returnedDirector = directorRepository.update(newDirector);
        assertEquals("newDirector", returnedDirector.getName());
        Director directorAfterSaveInDB = directorRepository.findById(1L).get();
        assertEquals("newDirector", directorAfterSaveInDB.getName());
    }

    @Test
    @DisplayName("Test delete by id in DirectorRepository")
    void testDeleteById() {
        directorRepository.save(newDirector);
        assertEquals(1, directorRepository.findAll().size());
        directorRepository.deleteById(1L);
        assertEquals(0, directorRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all in DirectorRepository")
    void testFindAll() {
        assertEquals(0, directorRepository.findAll().size());
        directorRepository.save(newDirector);
        assertEquals(1, directorRepository.findAll().size());
        directorRepository.save(secondDirector);
        assertEquals(2, directorRepository.findAll().size());
        directorRepository.save(thirdDirector);
        assertEquals(3, directorRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find director by id in DirectorRepository")
    void testFindById() {
        directorRepository.saveAll(List.of(newDirector, secondDirector, thirdDirector));
        assertEquals("newDirector", directorRepository.findById(1L).get().getName());
        assertEquals("thirdDirector", directorRepository.findById(3L).get().getName());
    }

    @Test
    @DisplayName("Test save List directors in DirectorRepository")
    void testSaveAll() {
        assertEquals(0, directorRepository.findAll().size());
        directorRepository.saveAll(List.of(newDirector, secondDirector, thirdDirector));
        assertEquals(3, directorRepository.findAll().size());
        assertEquals("secondDirector", directorRepository.findById(2L).get().getName());
    }
}