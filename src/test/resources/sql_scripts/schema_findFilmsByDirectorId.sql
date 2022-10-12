INSERT INTO films (DESCRIPTION, DURATION, NAME, RATE, RATING_MPA_ID, RELEASE_DATE)
VALUES ('test description 1', 10, 'test name 1', 10, 1, '2000-11-15'),
       ('test description 2', 20, 'test name 2', 20, 2, '2010-10-15'),
       ('test description 3', 30, 'test name 3', 5, 3, '2020-9-15');

INSERT INTO directors (NAME)
VALUES ('director 1'),
       ('director 2');

INSERT INTO directors_films (director_id, film_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 3);