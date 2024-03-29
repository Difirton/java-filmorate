DROP TABLE IF EXISTS users_rates_reviews;
DROP TABLE IF EXISTS users_films_marks;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS directors_films;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS users_likes_films;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS rating_mpa;
DROP TABLE IF EXISTS directors;

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
    rate DOUBLE,
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
CREATE TABLE rating_mpa
(
    id BIGINT auto_increment,
    title VARCHAR(5) NOT NULL,
        PRIMARY KEY (id)
);
CREATE TABLE directors
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE directors_films
(
    director_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL
);
CREATE TABLE reviews
(
    id BIGINT generated BY DEFAULT AS IDENTITY,
    content VARCHAR NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    useful INTEGER NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE users_rates_reviews
(
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    is_positive BOOLEAN NOT NULL
);
CREATE TABLE events
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    entity_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE users_films_marks
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    mark INTEGER NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE film_genres ADD CONSTRAINT uk_film_id_genre_id UNIQUE (film_id, genre_id);
ALTER TABLE genres ADD CONSTRAINT uk_genre_title UNIQUE (title);
ALTER TABLE user_friends ADD CONSTRAINT uk_user_friend UNIQUE (user_id, friend_id);
ALTER TABLE users ADD CONSTRAINT uk_user_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_user_login UNIQUE (login);
ALTER TABLE users_rates_reviews ADD CONSTRAINT uk_user_rate_review UNIQUE (user_id, review_id);
ALTER TABLE film_genres ADD CONSTRAINT fk_film_genres_film FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE;
ALTER TABLE film_genres ADD CONSTRAINT fk_film_genres_genre FOREIGN KEY (genre_id) REFERENCES genres ON DELETE CASCADE;
ALTER TABLE user_friends ADD CONSTRAINT fk_friend_id FOREIGN KEY (friend_id) REFERENCES users ON DELETE CASCADE;
ALTER TABLE user_friends ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE;
ALTER TABLE films ADD CONSTRAINT fk_rating_mpa_films FOREIGN KEY (rating_mpa_id) REFERENCES rating_mpa;
ALTER TABLE directors_films ADD CONSTRAINT fk_director_id FOREIGN KEY (director_id) REFERENCES directors ON DELETE CASCADE;
ALTER TABLE directors_films ADD CONSTRAINT fk_film_id FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE;
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_users FOREIGN KEY (user_id) REFERENCES users;
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_films FOREIGN KEY (film_id) REFERENCES films;
ALTER TABLE users_rates_reviews ADD CONSTRAINT fk_users_rates_reviews_reviews FOREIGN KEY (review_id) REFERENCES reviews ON DELETE CASCADE;
ALTER TABLE users_rates_reviews ADD CONSTRAINT fk_users_rates_reviews_users FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE;
ALTER TABLE events ADD CONSTRAINT fk_events_user_id FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE;
ALTER TABLE users_films_marks ADD CONSTRAINT fk_users_films_marks_films FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE;
ALTER TABLE users_films_marks ADD CONSTRAINT fk_users_films_marks_users FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE;

INSERT INTO rating_mpa (title) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');
INSERT INTO genres (title) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');