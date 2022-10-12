package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.EventRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryJdbcImpl implements EventRepository {

    private final JdbcOperations jdbcOperations;

    private final EventRepositoryMapper eventMapper;

    private static final String SQL_INSERT_ALL_FIELDS = "INSERT INTO events (timestamp, user_id, event_type, operation, " +
            "entity_id) VALUES (?,?,?,?,?)";

    private static final String SQL_UPDATE_ALL_FIELDS = "UPDATE events SET timestamp = ?, user_id = ?, event_type = ?," +
            "operation = ?, entity_id = ? WHERE id = ?";

    private static final String SQL_DELETE_BY_ID = "DELETE FROM events WHERE id = ?";

    private static final String SQL_SELECT_ALL = "SELECT * FROM events";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM events WHERE id = ?";

    private static final String SQL_SELECT_EVENTS_BY_USER_ID = "SELECT * FROM events WHERE user_id = ?";

    @Override
    public Event save(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ALL_FIELDS, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType().toString());
            ps.setString(4, event.getOperation().toString());
            ps.setLong(5, event.getEntityId());
            return ps;
        }, keyHolder);
        event.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    @Override
    public Event update(Event event) {
        jdbcOperations.update(SQL_UPDATE_ALL_FIELDS,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId(),
                event.getId());
        return event;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<Event> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL, eventMapper);
    }

    @Override
    public Optional findById(Long id) {
        return Optional.ofNullable(jdbcOperations.queryForObject(SQL_SELECT_BY_ID, eventMapper, id));
    }

    @Override
    public int[] saveAll(List<Event> events) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_ALL_FIELDS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, events.get(i).getTimestamp());
                        ps.setLong(2, events.get(i).getUserId());
                        ps.setString(3, events.get(i).getEventType().toString());
                        ps.setString(4, events.get(i).getOperation().toString());
                        ps.setLong(5, events.get(i).getEntityId());
                    }
                    public int getBatchSize() {
                        return events.size();
                    }
                });
    }

    @Override
    public List<Event> findEventsByUserId(Long userId) {
        return this.jdbcOperations.query(SQL_SELECT_EVENTS_BY_USER_ID, eventMapper, userId);
    }
}
