package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_EventRepositoryTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventRepositoryJdbcImplTest {
    private Event newEvent;
    private Event secondEvent;
    private Event thirdEvent;
    @Autowired
    EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        newEvent = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(1L)
                .eventType(EventType.MARK)
                .operation(Operation.ADD)
                .entityId(1L)
                .build();
        secondEvent = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(2L)
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(2L)
                .build();
        thirdEvent = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(1L)
                .eventType(EventType.MARK)
                .operation(Operation.REMOVE)
                .entityId(2L)
                .build();
    }

    @Test
    @DisplayName("Test save in EventRepository")
    void testSave() {
        Event returnedEvent = eventRepository.save(newEvent);
        assertEquals(1, returnedEvent.getId());
        assertEquals(EventType.MARK, returnedEvent.getEventType());
        Event eventAfterSaveInDB = eventRepository.findById(1L).get();
        assertEquals(Operation.ADD, eventAfterSaveInDB.getOperation());
    }

    @Test
    @DisplayName("Test update in EventRepository")
    void update() {
        eventRepository.save(newEvent);
        newEvent.setId(1L);
        newEvent.setEventType(EventType.REVIEW);
        Event returnedEvent = eventRepository.update(newEvent);
        assertEquals(EventType.REVIEW, returnedEvent.getEventType());
        Event eventAfterSaveInDB = eventRepository.findById(1L).get();
        assertEquals(EventType.REVIEW, eventAfterSaveInDB.getEventType());
    }

    @Test
    @DisplayName("Test delete by id in EventRepository")
    void testDeleteById() {
        eventRepository.save(newEvent);
        assertEquals(1, eventRepository.findAll().size());
        eventRepository.deleteById(1L);
        assertEquals(0, eventRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find all in EventRepository")
    void testFindAll() {
        assertEquals(0, eventRepository.findAll().size());
        eventRepository.save(newEvent);
        assertEquals(1, eventRepository.findAll().size());
        eventRepository.save(secondEvent);
        assertEquals(2, eventRepository.findAll().size());
        eventRepository.save(thirdEvent);
        assertEquals(3, eventRepository.findAll().size());
    }

    @Test
    @DisplayName("Test find director by id in EventRepository")
    void testFindById() {
        eventRepository.saveAll(List.of(newEvent, secondEvent, thirdEvent));
        assertEquals(EventType.MARK, eventRepository.findById(1L).get().getEventType());
        assertEquals(EventType.REVIEW, eventRepository.findById(2L).get().getEventType());
        assertEquals(EventType.MARK, eventRepository.findById(3L).get().getEventType());
    }

    @Test
    @DisplayName("Test save List directors in EventRepository")
    void testSaveAll() {
        assertEquals(0, eventRepository.findAll().size());
        eventRepository.saveAll(List.of(newEvent, secondEvent, thirdEvent));
        assertEquals(3, eventRepository.findAll().size());
        assertEquals(Operation.ADD, eventRepository.findById(2L).get().getOperation());
    }

    @Test
    @DisplayName("Test fund List of events by user id in EventRepository")
    void testFindEventsByUserId() {
        eventRepository.saveAll(List.of(newEvent, secondEvent, thirdEvent));
        newEvent.setId(1L);
        secondEvent.setId(2L);
        thirdEvent.setId(3L);
        assertEquals(2, eventRepository.findEventsByUserId(1L).size());
        assertEquals(newEvent, eventRepository.findEventsByUserId(1L).get(0));
        assertEquals(thirdEvent, eventRepository.findEventsByUserId(1L).get(1));
        assertEquals(1, eventRepository.findEventsByUserId(2L).size());
        assertEquals(secondEvent, eventRepository.findEventsByUserId(2L).get(0));
    }
}