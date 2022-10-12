package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRateRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepositoryJdbcImpl;
import ru.yandex.practicum.filmorate.repository.impl.UserRepositoryJdbcImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {
    @MockBean
    private ReviewRepository mockReviewRepository;
    @MockBean(FilmRepositoryJdbcImpl.class)
    private FilmRepository mockFilmRepository;
    @MockBean(UserRepositoryJdbcImpl.class)
    private UserRepository mockUserRepository;
    @MockBean
    private  ReviewRateRepository mockReviewRateRepository;
    @Autowired
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("mail@mail.ru").login("testuser").name("Test User")
                .birthday(LocalDate.of(2000, 2, 22)).build();
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        Film film = Film.builder().id(1L).name("Test film").description("Test Film Description")
                .releaseDate(LocalDate.of(2000, 2, 22)).duration(180).rate(0.0)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build()).build();
        when(mockFilmRepository.findById(1L)).thenReturn(Optional.of(film));

        Review review = Review.builder().id(1L).content("TestGoodReview").filmId(1L).userId(1L).isPositive(true).build();
        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(review));
    }

    @Test
    @DisplayName("Test throw not found user exception, when creating review by non-existent user")
    void testCreateNotFoundUser() {
        Review failUserReview = Review.builder().id(1L).content("FailUserReview")
                .filmId(1L).userId(-1L).isPositive(true).build();
        assertThrows(UserNotFoundException.class, () -> reviewService.createReview(failUserReview));
    }

    @Test
    @DisplayName("Test throw not found film exception, when creating review for non-existent film")
    void testCreateNotFoundFilm() {
        Review failFilmReview = Review.builder().id(1L).content("FailFilmReview")
                .filmId(-1L).userId(1L).isPositive(true).build();
        assertThrows(FilmNotFoundException.class, () -> reviewService.createReview(failFilmReview));
    }

    @Test
    @DisplayName("Test throw not found user exception, when updating review by non-existent user")
    void testUpdateNotFoundUser() {
        Review failUserReview = Review.builder().id(1L).content("FailUserReview")
                .filmId(1L).userId(-1L).isPositive(true).build();
        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview(1L, failUserReview));
    }

    @Test
    @DisplayName("Test throw not found film exception, when updating review for non-existent user")
    void testUpdateNotFoundFilm() {
        Review failFilmReview = Review.builder().id(1L).content("FailFilmReview")
                .filmId(-1L).userId(1L).isPositive(true).build();
        assertThrows(FilmNotFoundException.class, () -> reviewService.updateReview(1L, failFilmReview));
    }

    @Test
    @DisplayName("Test throw not found review exception, when getting non-existent review")
    void testGetNotFound() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(-1L));
    }

    @Test
    @DisplayName("Test throw not found review exception, when updating non-existent review")
    void testUpdateNotFound() {
        Review updatedReview = Review.builder().id(1L).content("updatedReview")
                .filmId(1L).userId(1L).isPositive(true).build();
        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(-1L, updatedReview));
    }

    @Test
    @DisplayName("Test add like, useful should be 1")
    void testAddLike() {
        reviewService.addLike(1L,1L);
        assertEquals(1, reviewService.getReviewById(1L).getUseful());
    }

    @Test
    @DisplayName("Test add dislike, useful should be -1")
    void testAddDislike() {
        reviewService.addDislike(1L,1L);
        assertEquals(-1, reviewService.getReviewById(1L).getUseful());
    }

    @Test
    @DisplayName("Test add like, useful should be 1")
    void testRemoveLike() {
        reviewService.addLike(1L,1L);
        assertEquals(1, reviewService.getReviewById(1L).getUseful());
        reviewService.removeLike(1L,1L);
        assertEquals(0, reviewService.getReviewById(1L).getUseful());
    }

    @Test
    @DisplayName("Test add dislike, useful should be -1")
    void testRemoveDislike() {
        reviewService.addDislike(1L,1L);
        assertEquals(-1, reviewService.getReviewById(1L).getUseful());
        reviewService.removeDislike(1L,1L);
        assertEquals(0, reviewService.getReviewById(1L).getUseful());
    }

    @Test
    @DisplayName("Test throw not found user exception, when adding like by non-existent user")
    void testAddLikeNotFoundUser() {
        assertThrows(UserNotFoundException.class, () -> reviewService.addLike(1L, -1L));
    }

    @Test
    @DisplayName("Test throw not found user exception, when adding like for non-existent review")
    void testAddLikeNotFoundReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.addLike(-1L, 1L));
    }

    @Test
    @DisplayName("Test throw not found user exception, when adding like by non-existent user")
    void testAddDisLikeNotFoundUser() {
        assertThrows(UserNotFoundException.class, () -> reviewService.addDislike(1L, -1L));
    }

    @Test
    @DisplayName("Test throw not found user exception, when adding like for non-existent review")
    void testAddDislikeNotFoundReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.addDislike(-1L, 1L));
    }
}