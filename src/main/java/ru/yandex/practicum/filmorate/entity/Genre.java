package ru.yandex.practicum.filmorate.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "title")
@ToString(exclude = "films")
@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", unique = true)
    private String title;

    @OneToMany
    @JoinTable(name = "film_genres",
            uniqueConstraints = @UniqueConstraint(name = "UNQ_FILM_ID_GENRE_ID", columnNames = {"film_id", "genre_id"}),
            joinColumns = @JoinColumn(name = "film_id",
                    foreignKey = @ForeignKey(name = "FK_FILM_ID"),
                    nullable = false),
            inverseJoinColumns = @JoinColumn(name = "genre_id",
                    foreignKey = @ForeignKey(name = "FK_GENRE_ID"),
                    nullable = false)
    )
    private List<Film> films = new ArrayList<>();
}
