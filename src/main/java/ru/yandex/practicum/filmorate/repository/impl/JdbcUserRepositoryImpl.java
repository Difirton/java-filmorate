package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Optional;

@Repository
public class JdbcUserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepositoryMapper userMapper;

    @Autowired
    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate, UserRepositoryMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", userMapper);
    }

    @Override
    public List<User> findAllFriendsUser(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE id IN (SELECT friend_id FROM user_friends WHERE user_id = ?)",
                userMapper, id);
    }

    @Override
    public List<User> findCommonUsersFriends(Long id, Long otherId) {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE id IN (SELECT * FROM " +
                        "(SELECT friend_id FROM user_friends WHERE user_id = ?) INNER JOIN" +
                        "(SELECT friend_id FROM user_friends WHERE user_id = ?) USING (friend_id))",
                userMapper, id, otherId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM users where id = ?", userMapper, id));
    }

    @Override
    public int[] saveAll(List<User> users) {
        return this.jdbcTemplate.batchUpdate("INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)",
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
    public int[][] updateAll(List<User> users) {
        return jdbcTemplate.batchUpdate(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
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
