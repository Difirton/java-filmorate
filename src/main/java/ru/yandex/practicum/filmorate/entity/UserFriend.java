package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "friend"})
public class UserFriend {
    private Long id;

    @NotNull
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private User user;

    @NotNull
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private User friend;

    private boolean approved;

    public static UserFriendBuilder builder() {
        return new UserFriendBuilder();
    }

    public static class UserFriendBuilder {
        private Long id;

        @NotNull
        private User user;

        @NotNull
        private User friend;

        private boolean approved;

        private UserFriendBuilder() { }

        public UserFriendBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserFriendBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserFriendBuilder friend(User friend) {
            this.friend = friend;
            return this;
        }

        public UserFriendBuilder approved(boolean approved) {
            this.approved = approved;
            return this;
        }

        public UserFriend build() {
            return new UserFriend(id, user, friend, approved);
        }
    }
}
