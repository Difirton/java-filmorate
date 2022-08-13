package ru.yandex.practicum.filmorate.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmRepositoryTest {
    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        film1 = Film.builder().id(1L).name("name film 1").description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25)).duration(100).build();
        film2 = Film.builder().id(2L).name("name film 2").description("description film 2")
                .releaseDate(LocalDate.of(1997, 5, 1)).duration(300).build();
        film3 = Film.builder().id(3L).name("name film 3").description("description film 3")
                .releaseDate(LocalDate.of(2007, 10, 12)).duration(200).build();
        filmRepository.saveAll(List.of(film1, film2, film3));
        user1 = User.builder().id(1L).email("example@gmail.ru").login("231ffdsf32").name("Test1 Test1")
                .birthday(LocalDate.of(1990, 5, 1)).build();
        user2 = User.builder().id(2L).email("mail@mail.ru").login("FFad23fdas").name("Test2 Tes2")
                .birthday(LocalDate.of(1981, 11, 15)).build();
        user3 = User.builder().id(3L).email("example@mail.com").login("648448fafaf22").name("Test3 Tes3")
                .birthday(LocalDate.of(1995, 8, 20)).build();
        user4 = User.builder().id(4L).email("example@yandex.com").login("64GGfafaf22").name("Test4 Tes4")
                .birthday(LocalDate.of(1965, 9, 23)).build();
        userRepository.saveAll(List.of(user1, user2, user3, user4));
    }

    @Test
    @DisplayName("Test CRUD of FilmRepository, expected ok")
    public void testCreateReadDeleteFilmRepository() {
        Iterable<Film> filmsBeforeUpdate = filmRepository.findAll();
        Assertions.assertThat(filmsBeforeUpdate).extracting(Film::getName).contains("name film 1");
        Film filmToUpdate = filmRepository.findById(1L).get();
        filmToUpdate.setName("updated");
        filmRepository.save(filmToUpdate);
        Film filmAfterUpdate = filmRepository.findById(1L).get();
        Assertions.assertThat(filmAfterUpdate).extracting(Film::getName).isEqualTo("updated");
        filmRepository.deleteAll();
        Assertions.assertThat(filmRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test find 2 popular films")
    public void testFindPopularFilmsWhenLikesHaveTwoFilms() {
        film1.addUserLike(user1);
        film2.addUserLike(user2);
        film2.addUserLike(user3);
        film2.addUserLike(user1);
        filmRepository.saveAll(List.of(film1, film2, film3));
        userRepository.saveAll(List.of(user1, user2, user3, user4));
        List<Film> popularFilms = filmRepository.findPopularFilms(2);
        Assertions.assertThat(popularFilms).isEqualTo(List.of(film1, film2));
    }

    @Test
    @DisplayName("Test pagination of method findPopularFilms")
    public void testPaginationFindPopularFilmsWhenLikesHaveTwoFilms() {
        film1.addUserLike(user1);
        film2.addUserLike(user2);
        film2.addUserLike(user3);
        film2.addUserLike(user1);
        filmRepository.saveAll(List.of(film1, film2, film3));
        userRepository.saveAll(List.of(user1, user2, user3, user4));
        List<Film> popularFilms = filmRepository.findPopularFilms(1);
        Assertions.assertThat(popularFilms).isEqualTo(List.of(film2));
    }

    @Test
    @DisplayName("Test delete like of film")
    public void testDeleteLike() {
        film1.addUserLike(user1);
        film2.addUserLike(user2);
        film2.addUserLike(user3);
        film2.addUserLike(user1);
        filmRepository.saveAll(List.of(film1, film2, film3));
        userRepository.saveAll(List.of(user1, user2, user3, user4));
        film1.removeUserLike(user1);
        filmRepository.save(film1);
        userRepository.save(user1);
        List<Film> popularFilms = filmRepository.findPopularFilms(10);
        Assertions.assertThat(popularFilms).isEqualTo(List.of(film2));
    }
}