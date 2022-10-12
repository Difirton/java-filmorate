package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.repository.UserFilmMarkRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_ReviewRepositoryJdbcImplTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserFilmMarkRepositoryJdbcImplTest {
    User user1;
    User user2;
    Film film1;
    Film film2;
    private UserFilmMark user1Film1Mark5;
    private UserFilmMark user1Film2Mark8;
    private UserFilmMark user2Film1Mark4;

    @Autowired
    UserFilmMarkRepository userFilmMarkRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("test_1")
                .login("test_1")
                .email("test1@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("test_2")
                .login("test_2")
                .email("test2@mail.ru")
                .build();
        film1 = Film.builder()
                .id(1L)
                .name("test name 1")
                .description("test description 1")
                .rate(10.0)
                .ratingMPA(RatingMPA.builder()
                        .id(1L)
                        .build())
                .duration(10)
                .releaseDate(LocalDate.of(2000, 11, 15))
                .build();
        film2 = Film.builder()
                .id(2L)
                .name("test name 2")
                .description("test description 2")
                .rate(20.0)
                .ratingMPA(RatingMPA.builder()
                        .id(2L)
                        .build())
                .duration(20)
                .releaseDate(LocalDate.of(2010, 10, 15))
                .build();
        user1Film1Mark5 = UserFilmMark.builder()
                .user(user1)
                .film(film1)
                .mark(5)
                .build();
        user2Film1Mark4 = UserFilmMark.builder()
                .user(user2)
                .film(film1)
                .mark(4)
                .build();
        user1Film2Mark8 = UserFilmMark.builder()
                .user(user1)
                .film(film2)
                .mark(8)
                .build();
    }


    @Test
    @DisplayName("Test find mark of film by user and film ids in UserFilmMarkRepository")
    void testFindByUserIdAndFilmId() {
        userFilmMarkRepository.save(user1Film2Mark8);
        UserFilmMark userFilmMark = userFilmMarkRepository.findByUserIdAndFilmId(1L, 2L).get();
        assertEquals(8, userFilmMark.getMark());
    }

    @Test
    @DisplayName("Test save in UserFilmMarkRepository")
    void testSave() {
        userFilmMarkRepository.save(user1Film2Mark8);
        assertEquals(1, user1Film2Mark8.getId());
        UserFilmMark userFilmMarkAfterSaveInDB = userFilmMarkRepository.findById(1L).get();
        assertEquals(user1, userFilmMarkAfterSaveInDB.getUser());
        assertEquals(film2, userFilmMarkAfterSaveInDB.getFilm());
        assertEquals(8, userFilmMarkAfterSaveInDB.getMark());
    }

    @Test
    @DisplayName("Test update in UserFilmMarkRepository")
    void testUpdate() {
        userFilmMarkRepository.save(user1Film2Mark8);
        user1Film2Mark8.setId(1L);
        user1Film2Mark8.setUser(user2);
        user1Film2Mark8.setFilm(film1);
        user1Film2Mark8.setMark(3);
        userFilmMarkRepository.update(user1Film2Mark8);
        UserFilmMark userFilmMarkAfterSaveInDB = userFilmMarkRepository.findById(1L).get();
        assertEquals(user2, userFilmMarkAfterSaveInDB.getUser());
        assertEquals(film1, userFilmMarkAfterSaveInDB.getFilm());
        assertEquals(3, userFilmMarkAfterSaveInDB.getMark());
    }

    @Test
    @DisplayName("Test delete by id in UserFilmMarkRepository")
    void testDeleteById() {
        userFilmMarkRepository.save(user1Film2Mark8);
        assertEquals(1, userFilmMarkRepository.findAll().size());
        userFilmMarkRepository.deleteById(1L);
        assertEquals(0, userFilmMarkRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all marks in UserFilmMarkRepository")
    void testFindAll() {
        assertEquals(0, userFilmMarkRepository.findAll().size());
        userFilmMarkRepository.save(user1Film2Mark8);
        assertEquals(1, userFilmMarkRepository.findAll().size());
        userFilmMarkRepository.save(user1Film1Mark5);
        assertEquals(2, userFilmMarkRepository.findAll().size());
        userFilmMarkRepository.save(user1Film1Mark5);
        assertEquals(3, userFilmMarkRepository.findAll().size());
    }

    @Test
    void testFindById() {
        userFilmMarkRepository.save(user1Film2Mark8);
        userFilmMarkRepository.save(user1Film1Mark5);
        userFilmMarkRepository.save(user1Film1Mark5);
        assertEquals(user1, userFilmMarkRepository.findById(1L).get().getUser());
        assertEquals(film1, userFilmMarkRepository.findById(2L).get().getFilm());
        assertEquals(5, userFilmMarkRepository.findById(3L).get().getMark());
    }

    @Test
    void saveAll() {
    }
}