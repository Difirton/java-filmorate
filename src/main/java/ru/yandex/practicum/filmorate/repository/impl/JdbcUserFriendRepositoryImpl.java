package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.config.mapper.UserFriendRepositoryLazyMapper;
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
    private final UserFriendRepositoryLazyMapper userFriendRepositoryLazyMapper;
    private static final String SQL_INSERT_ALL_FIELDS = "INSERT INTO user_friends (user_id, friend_id, approved) " +
            "VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_ALL_FIELDS = "UPDATE user_friends " +
            "SET user_id = ?, friend_id = ?, approved = ? WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE user_friends WHERE id = ?";
    private static final String SQL_DELETE_BY_USER_ID_AND_FRIEND_ID = "DELETE user_friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM user_friends";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM user_friends WHERE id = ?";

    @Autowired
    public JdbcUserFriendRepositoryImpl(JdbcOperations jdbcOperations, UserFriendRepositoryLazyMapper userFriendRepositoryLazyMapper) {
        this.jdbcOperations = jdbcOperations;
        this.userFriendRepositoryLazyMapper = userFriendRepositoryLazyMapper;
    }

    @Override
    public UserFriend save(UserFriend userFriend) {
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS,
                userFriend.getUser().getId(), userFriend.getFriend().getId(), userFriend.isApproved());
        return userFriend;
    }

    @Override
    @Transactional
    public UserFriend save(User user, User friend) {
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS, user.getId(), friend.getId(), true);
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS, friend.getId(), user.getId(), false);
        return UserFriend.builder().user(user).friend(friend).approved(false).build();
    }

    @Override
    @Transactional
    public UserFriend save(User user, User friend, boolean isApproved) {
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS, user.getId(), friend.getId(), isApproved);
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS, friend.getId(), user.getId(), isApproved);
        return UserFriend.builder().user(user).friend(friend).approved(isApproved).build();
    }

    @Override
    public UserFriend update(UserFriend userFriend) {
        this.jdbcOperations.update(SQL_UPDATE_ALL_FIELDS, userFriend.getUser().getId(), userFriend.getFriend().getId(),
                userFriend.isApproved(), userFriend.getId());
        return userFriend;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    @Transactional
    public int delete(UserFriend userFriend) {
        return this.jdbcOperations.update(SQL_DELETE_BY_USER_ID_AND_FRIEND_ID,
                userFriend.getUser().getId(), userFriend.getFriend().getId());
    }

    @Override
    public List<UserFriend> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL, userFriendRepositoryLazyMapper);
    }

    @Override
    public Optional<UserFriend> findById(Long id) {
        return Optional.ofNullable(jdbcOperations.queryForObject(SQL_SELECT_BY_ID, userFriendRepositoryLazyMapper, id));
    }

    @Override
    public int[] saveAll(List<UserFriend> usersFriends) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_ALL_FIELDS,
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
    public int[][] updateAll(List<UserFriend> usersFriends) {
        return jdbcOperations.batchUpdate(SQL_UPDATE_ALL_FIELDS,
                usersFriends, usersFriends.size(),
                (preparedStatement, userFriend) -> {
                    preparedStatement.setLong(1, userFriend.getUser().getId());
                    preparedStatement.setLong(2, userFriend.getFriend().getId());
                    preparedStatement.setBoolean(3, userFriend.isApproved());
                    preparedStatement.setLong(4, userFriend.getId());
                });
    }
}
