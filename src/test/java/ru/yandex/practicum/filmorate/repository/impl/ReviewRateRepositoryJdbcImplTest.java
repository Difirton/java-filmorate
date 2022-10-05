package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.ReviewRateRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql_scripts/schema_ReviewRateRepositoryJdbcImplTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewRateRepositoryJdbcImplTest {
    private User firstUser;
    private User secondUser;
    private Review firstReview;
    private Review secondReview;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRateRepository reviewRateRepository;


    @BeforeEach
    void setUp() {
        firstUser = userRepository.findById(1L).get();
        secondUser = userRepository.findById(2L).get();
        firstReview = reviewRepository.findById(1L).get();
        secondReview = reviewRepository.findById(2L).get();
    }

    @Test
    @DisplayName("Test save review like in ReviewRateRepository")
    void testSavePositive() {
        reviewRateRepository.save(firstUser, firstReview, true);
        assertEquals(1, reviewRepository.findById(1L).get().getUseful());
        assertEquals(1, reviewRepository.findById(1L).get().getUsersRates().size());
    }

    @Test
    @DisplayName("Test save review dislike in ReviewRateRepository")
    void testSaveNegative() {
        reviewRateRepository.save(secondUser, firstReview, false);
        assertEquals(-1, reviewRepository.findById(1L).get().getUseful());
        assertEquals(1, reviewRepository.findById(1L).get().getUsersRates().size());
    }

    @Test
    @DisplayName("Test delete review like in ReviewRateRepository")
    void testDeletePositive() {
        reviewRateRepository.save(firstUser, firstReview, true);
        reviewRateRepository.save(secondUser, firstReview, true);
        assertEquals(2, reviewRepository.findById(1L).get().getUseful());
        assertEquals(2, reviewRepository.findById(1L).get().getUsersRates().size());
        reviewRateRepository.delete(firstUser.getId(), firstReview.getId(), true);
        assertEquals(1, reviewRepository.findById(1L).get().getUseful());
        assertEquals(1, reviewRepository.findById(1L).get().getUsersRates().size());
    }

    @Test
    @DisplayName("Test delete review like in ReviewRateRepository")
    void testDeleteNegative() {
        reviewRateRepository.save(firstUser, firstReview, false);
        reviewRateRepository.save(secondUser, firstReview, false);
        assertEquals(-2, reviewRepository.findById(1L).get().getUseful());
        assertEquals(2, reviewRepository.findById(1L).get().getUsersRates().size());
        reviewRateRepository.delete(firstUser.getId(), firstReview.getId(), false);
        assertEquals(-1, reviewRepository.findById(1L).get().getUseful());
        assertEquals(1, reviewRepository.findById(1L).get().getUsersRates().size());
    }

    @Test
    @DisplayName("Test find rates by review id in ReviewRateRepository")
    void testFindRatesByReviewId() {
        reviewRateRepository.save(firstUser, firstReview, true);
        reviewRateRepository.save(secondUser, firstReview, false);
        reviewRateRepository.save(firstUser, secondReview, true);
        assertEquals(2, reviewRateRepository.getByReviewId(1L).size());
        assertEquals(1, reviewRateRepository.getByReviewId(2L).size());
    }
}