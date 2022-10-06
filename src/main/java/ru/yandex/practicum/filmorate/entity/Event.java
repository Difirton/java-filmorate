package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long id;
    private Long timestamp;
    private Long userId;
    private EventTypes eventType;
    private Operations operation;
    private Long entityId;
}
