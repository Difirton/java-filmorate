package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}
