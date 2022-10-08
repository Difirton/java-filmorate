INSERT INTO users (email, login, name)
VALUES ('test1@mail.ru', 'test_1', 'test_1'), ('test2@mail.ru', 'test_2', 'test_2');
INSERT INTO films (DESCRIPTION, DURATION, NAME, RATE, RATING_MPA_ID, RELEASE_DATE)
VALUES ('test description 1', 10, 'test name 1', 10, 1, '2000-11-15'),
       ('test description 2', 20, 'test name 2', 20, 2, '2010-10-15');
INSERT INTO reviews(content, is_positive, user_id, film_id, useful)
VALUES ('TestGoodReview', true, 1, 1, 0), ('TestBadReview', false, 2, 2, 0);