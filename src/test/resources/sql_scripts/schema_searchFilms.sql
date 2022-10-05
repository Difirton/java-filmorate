INSERT INTO films (DESCRIPTION, DURATION, NAME, RATE, RATING_MPA_ID, RELEASE_DATE)
VALUES ('Desc 1', 10, 'Lucky man', 10, 1, '2001-11-15'),
       ('Desc 2', 20, 'Toy`s history 2', 20, 2, '2010-10-15'),
       ('Desc 3', 30, 'Toy`s history 1', 25, 3, '2020-9-15');

INSERT INTO directors (NAME)
VALUES ('Luck Hitton'),
       ('Mark Walberg');

INSERT INTO directors_films (director_id, film_id)
VALUES (1, 2),
       (2, 1),
       (2, 3);