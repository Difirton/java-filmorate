package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;

import java.util.List;

public interface UserFriendRepository extends StandardCRUDRepository<UserFriend>{

    UserFriend save(User user, User friend);

    UserFriend save(User user, User friend, boolean isApproved);
    int delete(UserFriend userFriend);

    int[][] updateAll(List<UserFriend> usersFriends);
}
