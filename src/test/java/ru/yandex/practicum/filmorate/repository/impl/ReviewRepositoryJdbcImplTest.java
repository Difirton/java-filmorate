package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_ReviewRepositoryJdbcImplTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewRepositoryJdbcImplTest {
    private Review goodReview;
    private Review badReview;
    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        goodReview = Review.builder()
                .id(1L)
                .content("TestGoodReview")
                .filmId(1L)
                .userId(1L)
                .isPositive(true)
                .build();
        badReview = Review.builder()
                .id(2L)
                .content("TestBadReview")
                .filmId(2L)
                .userId(2L)
                .isPositive(false)
                .build();
    }

    @Test
    @DisplayName("Test save review in ReviewRepository")
    void testSave() {
        reviewRepository.save(goodReview);
        assertEquals("TestGoodReview", reviewRepository.findById(1L).get().getContent());
        assertEquals(1, reviewRepository.findById(1L).get().getFilmId());
        assertEquals(1, reviewRepository.findById(1L).get().getUserId());
        assertEquals(true, reviewRepository.findById(1L).get().getIsPositive());
        assertEquals(0, reviewRepository.findById(1L).get().getUseful());
    }

    @Test
    @DisplayName("Test update review in ReviewRepository")
    void testUpdate() {
        reviewRepository.save(goodReview);
        goodReview.setContent("NotSoGoodReview");
        goodReview.setIsPositive(false);
        reviewRepository.update(goodReview);
        assertEquals("NotSoGoodReview", reviewRepository.findById(1L).get().getContent());
        assertEquals(1, reviewRepository.findById(1L).get().getFilmId());
        assertEquals(1, reviewRepository.findById(1L).get().getUserId());
        assertEquals(false, reviewRepository.findById(1L).get().getIsPositive());
        assertEquals(0, reviewRepository.findById(1L).get().getUseful());
    }

    @Test
    @DisplayName("Test delete review by id in ReviewRepository")
    void testDeleteById() {
        reviewRepository.save(goodReview);
        assertEquals(1, reviewRepository.findAll().size());
        reviewRepository.deleteById(1L);
        assertEquals(0, reviewRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all reviews in ReviewRepository")
    void testFindAll() {
        reviewRepository.save(goodReview);
        assertEquals(1, reviewRepository.findAll().size());
        reviewRepository.save(badReview);
        assertEquals(2, reviewRepository.findAll().size());
        assertEquals(goodReview, reviewRepository.findAll().get(0));
        assertEquals(badReview, reviewRepository.findAll().get(1));
    }

    @Test
    @DisplayName("Test find review by id ReviewRepository")
    void testFindById() {
        reviewRepository.save(goodReview);
        reviewRepository.save(badReview);
        assertEquals(goodReview, reviewRepository.findById(1L).get());
        assertEquals(badReview, reviewRepository.findById(2L).get());
    }

    @Test
    @DisplayName("Save all reviews in ReviewRepository")
    void testSaveAll() {
        reviewRepository.saveAll(List.of(goodReview, badReview));
        assertEquals(goodReview, reviewRepository.findById(1L).get());
        assertEquals(badReview, reviewRepository.findById(2L).get());
    }

    @Test
    @DisplayName("Save all reviews in ReviewRepository")
    void testFindReviewsByFilmId() {
        reviewRepository.save(goodReview);
        reviewRepository.save(badReview);
        assertEquals(1, reviewRepository.findReviewsByFilmId(1L, 1).size());
        assertEquals(List.of(goodReview), reviewRepository.findReviewsByFilmId(1L, 1));
    }
}