package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.UserRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {
    private final JdbcOperations jdbcOperations;
    private final UserRepositoryMapper userMapper;
    private static final String SQL_INSERT_ALL_FIELDS = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?,?,?,?)";
    private static final String SQL_UPDATE_ALL_FIELDS = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM users";
    private static final String SQL_SELECT_ALL_USERS_FRIENDS = "SELECT * FROM users WHERE id IN " +
            "(SELECT friend_id FROM user_friends WHERE user_id = ?)";
    private static final String SQL_SELECT_COMMON_FRIENDS = "SELECT * FROM users WHERE id IN (SELECT * FROM " +
                    "(SELECT friend_id FROM user_friends WHERE user_id = ?) INNER JOIN" +
                    "(SELECT friend_id FROM user_friends WHERE user_id = ?) USING (friend_id))";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM users where id = ?";

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ALL_FIELDS, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbcOperations.update(SQL_UPDATE_ALL_FIELDS,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<User> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL, userMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(jdbcOperations.queryForObject(SQL_SELECT_BY_ID, userMapper, id));
    }

    @Override
    public int[] saveAll(List<User> users) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_ALL_FIELDS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, users.get(i).getEmail());
                        preparedStatement.setString(2, users.get(i).getLogin());
                        preparedStatement.setString(3, users.get(i).getName());
                        preparedStatement.setDate(4, Date.valueOf(users.get(i).getBirthday()));
                    }
                    public int getBatchSize() {
                        return users.size();
                    }
                });
    }

    @Override
    public List<User> findAllFriendsUser(Long id) {
        return jdbcOperations.query(SQL_SELECT_ALL_USERS_FRIENDS, userMapper, id);
    }

    @Override
    public List<User> findCommonUsersFriends(Long id, Long otherId) {
        return jdbcOperations.query(SQL_SELECT_COMMON_FRIENDS, userMapper, id, otherId);
    }

    @Override
    public int[][] updateAll(List<User> users) {
        return jdbcOperations.batchUpdate(SQL_UPDATE_ALL_FIELDS,
                users, users.size(),
                (preparedStatement, user) -> {
                    preparedStatement.setString(1, user.getEmail());
                    preparedStatement.setString(2, user.getLogin());
                    preparedStatement.setString(3, user.getName());
                    preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
                    preparedStatement.setLong(5, user.getId());
                });
    }
}
