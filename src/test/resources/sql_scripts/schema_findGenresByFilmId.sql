INSERT INTO films (DESCRIPTION, DURATION, NAME, RATE, RATING_MPA_ID)
VALUES ( 'test description', 10, 'test name', 10, 1);
INSERT INTO genres (title) VALUES ('test'), ('test_2');

INSERT INTO film_genres (film_id, genre_id) VALUES ( 1, 7), (1, 8)