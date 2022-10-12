package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
@ToString(exclude = {"likesFilms", "friends"})
public class User {
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @NotBlank(message = "Login should not be blank")
    @Pattern(regexp = "[a-zA-Z0-9_.]*", message = "Login should not contain spaces")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<UserFriend> friends = new ArrayList<>();

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<UserFilmMark> marksFilms = new ArrayList<>();

    public void addMarkFilm(UserFilmMark userFilmMark) {
        marksFilms.add(userFilmMark);
        userFilmMark.getFilm().getUsersMarks().add(userFilmMark);
    }

    public void removeMarkFilm(UserFilmMark userFilmMark) {
        marksFilms.remove(userFilmMark);
        userFilmMark.getFilm().getUsersMarks().remove(userFilmMark);
    }

    public void addFriend(User user) {
        UserFriend newFriend = UserFriend.builder()
                .user(this)
                .friend(user)
                .approved(true).build();
        friends.add(newFriend);
    }

    public void removeFriend(User user) {
        UserFriend removedFriend = UserFriend.builder()
                .user(this)
                .friend(user).build();
        friends.remove(removedFriend);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email should not be blank")
        private String email;

        @NotBlank(message = "Login should not be blank")
        @Pattern(regexp = "[a-zA-Z0-9_.]*", message = "Login should not contain spaces")
        private   String login;

        private String name;

        @Past
        private LocalDate birthday;

        private List<UserFriend> friends = new ArrayList<>();

        private List<UserFilmMark> marksFilms = new ArrayList<>();

        private UserBuilder() { }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder login(String login) {
            this.login = login;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder birthday(@Past LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public UserBuilder friends(List<UserFriend> friends) {
            this.friends = friends;
            return this;
        }

        public UserBuilder marksFilms(List<UserFilmMark> marksFilms) {
            this.marksFilms = marksFilms;
            return this;
        }

        public User build() {
            return new User(id, email, login, name, birthday, friends, marksFilms);
        }
    }
}
