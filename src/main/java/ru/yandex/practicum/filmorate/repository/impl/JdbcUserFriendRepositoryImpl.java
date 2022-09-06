package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;
import ru.yandex.practicum.filmorate.repository.UserFriendRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserFriendRepositoryImpl implements UserFriendRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired
    public JdbcUserFriendRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    @Transactional
    public UserFriend save(UserFriend userFriend) {
        this.jdbcOperations.update("INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                userFriend.getUser().getId(), userFriend.getFriend().getId(), userFriend.isApproved());
        return userFriend;
    }

    @Override
    @Transactional
    public UserFriend save(User user, User friend) {
        this.jdbcOperations.update("INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                user.getId(), friend.getId(), false);
        this.jdbcOperations.update("INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                friend.getId(), user.getId(), false);
        return UserFriend.builder().user(user).friend(friend).approved(false).build();
    }

    @Override
    @Transactional
    public UserFriend save(User user, User friend, boolean isApproved) {
        this.jdbcOperations.update("INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                user.getId(), friend.getId(), isApproved);
        this.jdbcOperations.update("INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                friend.getId(), user.getId(), isApproved);
        return UserFriend.builder().user(user).friend(friend).approved(isApproved).build();
    }

    @Override
    public UserFriend update(UserFriend userFriend) {
        this.jdbcOperations.update(
                "UPDATE user_friends SET user_id = ?, friend_id = ?, approved = ? WHERE id = ?",
                userFriend.getUser().getId(), userFriend.getFriend().getId(), userFriend.isApproved(),
                userFriend.getId());
        return userFriend;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update("DELETE user_friends WHERE id = ?", id);
    }

    @Override
    public List<UserFriend> findAll() {
        return null;
    }

    @Override
    public Optional<UserFriend> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public int[] saveAll(List<UserFriend> usersFriends) {
        return this.jdbcOperations.batchUpdate(
                "INSERT INTO user_friends (user_id, friend_id, approved) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setLong(1, usersFriends.get(i).getUser().getId());
                        preparedStatement.setLong(2, usersFriends.get(i).getFriend().getId());
                        preparedStatement.setBoolean(3, usersFriends.get(i).isApproved());
                    }
                    public int getBatchSize() {
                        return usersFriends.size();
                    }
                });
    }

    @Override
    @Transactional
    public int delete(UserFriend userFriend) {
        return this.jdbcOperations.update("DELETE user_friends WHERE user_id = ? AND friend_id = ?",
                userFriend.getUser().getId(), userFriend.getFriend().getId());
    }

    @Override
    public int[][] updateAll(List<UserFriend> usersFriends) {
        return jdbcOperations.batchUpdate(
                "UPDATE user_friends SET user_id = ?, friend_id = ?, approved = ? WHERE id = ?",
                usersFriends, usersFriends.size(),
                (preparedStatement, userFriend) -> {
                    preparedStatement.setLong(1, userFriend.getUser().getId());
                    preparedStatement.setLong(2, userFriend.getFriend().getId());
                    preparedStatement.setBoolean(3, userFriend.isApproved());
                    preparedStatement.setLong(4, userFriend.getId());
                });
    }
}
