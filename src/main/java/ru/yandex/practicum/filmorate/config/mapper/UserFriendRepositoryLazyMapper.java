package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFriendRepositoryLazyMapper implements RowMapper<UserFriend> {

    @Override
    public UserFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFriend.builder()
                .id(rs.getLong("id"))
                .user(User.builder()
                        .id(rs.getLong("user_id"))
                        .build())
                .friend(User.builder()
                        .id(rs.getLong("friend_id"))
                        .build())
                .approved(rs.getBoolean("approved"))
                .build();
    }
}
