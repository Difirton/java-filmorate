package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class EventRepositoryMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .timestamp(rs.getLong("timestamp"))
        .userId(rs.getLong("user_id"))
        .eventType(EventType.valueOf(rs.getString("event_type")))
        .operation(Operation.valueOf(rs.getString("operation")))
        .entityId(rs.getLong("entity_id"))
                .build();
    }
}
