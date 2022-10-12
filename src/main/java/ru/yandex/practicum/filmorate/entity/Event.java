package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @JsonProperty(value = "eventId")
    private Long id;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public static class EventBuilder {
        private Long id;
        private Long timestamp;
        private Long userId;
        private EventType eventType;
        private Operation operation;
        private Long entityId;

        private EventBuilder() { }

        public EventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EventBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public EventBuilder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public EventBuilder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public EventBuilder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Event build() {
            return new Event(id, timestamp, userId, eventType, operation, entityId);
        }
    }
}
