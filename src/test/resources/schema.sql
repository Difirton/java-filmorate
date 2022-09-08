DROP TABLE IF EXISTS FILM_GENRES;
DROP TABLE IF EXISTS USERS_LIKES_FILMS;
DROP TABLE IF EXISTS GENRES;
DROP TABLE IF EXISTS USER_FRIENDS;
DROP TABLE IF EXISTS FILMS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS RATING_MPA;

CREATE TABLE film_genres
(
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL
);
CREATE TABLE films
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    description VARCHAR(200),
    duration INTEGER,
    name VARCHAR(255) NOT NULL,
    rate INTEGER,
    release_date DATE,
    rating_mpa_id BIGINT,
        PRIMARY KEY (id)
);
CREATE TABLE genres
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    title VARCHAR(255),
        PRIMARY KEY (id)
);
CREATE TABLE user_friends
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    approved BOOLEAN NOT NULL,
    friend_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
        PRIMARY KEY (id)
);
CREATE TABLE users
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    birthday DATE,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
        PRIMARY KEY (id)
);
CREATE TABLE users_likes_films
(
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL
);
CREATE TABLE rating_mpa
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    title VARCHAR(5) NOT NULL,
        PRIMARY KEY (id)
);

ALTER TABLE film_genres ADD CONSTRAINT uk_film_id_genre_id UNIQUE (film_id, genre_id);
ALTER TABLE genres ADD CONSTRAINT uk_genre_title UNIQUE (title);
ALTER TABLE user_friends ADD CONSTRAINT uk_user_friend UNIQUE (user_id, friend_id);
ALTER TABLE users ADD CONSTRAINT uk_user_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_user_login UNIQUE (login);
ALTER TABLE user_friends ADD CONSTRAINT fk_friend_id FOREIGN KEY (friend_id) REFERENCES users;
ALTER TABLE user_friends ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users;
ALTER TABLE users_likes_films ADD CONSTRAINT fk_users_likes_film_id FOREIGN KEY (film_id) REFERENCES users;
ALTER TABLE users_likes_films ADD CONSTRAINT fk_films_likes_user_id FOREIGN KEY (user_id) REFERENCES films;
ALTER TABLE films ADD CONSTRAINT fk_rating_mpa_films FOREIGN KEY (rating_mpa_id) REFERENCES rating_mpa;
INSERT INTO rating_mpa (title) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');
INSERT INTO genres (title) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');