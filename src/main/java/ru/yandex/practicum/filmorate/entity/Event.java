package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.entity.constant.EventTypes;
import ru.yandex.practicum.filmorate.entity.constant.Operations;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventTypes eventType;
    private Operations operation;
    private Long entityId;
}
