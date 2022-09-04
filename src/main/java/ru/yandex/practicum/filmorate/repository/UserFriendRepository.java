package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;

import java.util.List;

public interface UserFriendRepository {

    UserFriend save(UserFriend userFriend);

    UserFriend save(User user, User friend);

    UserFriend save(User user, User friend, boolean isApproved);

    int update(UserFriend userFriend);

    int deleteById(Long id);

    int[] saveAll(List<UserFriend> usersFriends);
    int delete(UserFriend userFriend);

    int[][] updateAll(List<UserFriend> usersFriends);
}
