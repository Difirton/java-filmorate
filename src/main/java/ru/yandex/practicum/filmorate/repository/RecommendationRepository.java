package ru.yandex.practicum.filmorate.repository;

import java.util.List;

/**
 * An interface supplies the only method, that returns
 * ID's of films recommended for user
 *
 * @author Alexey Poltavsky
 */
public interface RecommendationRepository {

    /**
     *  default count of recommendations
     */
    int DEFAULT_RECOMMENDATIONS_COUNT = 10;

    /**
     * returns count recommendation for the user having id userID
     * @param userID user identifier
     * @param count max size of filmID List
     * @return List of filmID. Size of List less or equals count
     */
    List<Long> findRecommendationsByUser(Long userID, int count);

    /**
     * returns DEFAULT_RECOMMENDATIONS_COUNT recommendation for the user having id userID
     * @param userID user identifier
     * @return List of filmID. Size of List less or equals DEFAULT_RECOMMENDATIONS_COUNT
     */
    List<Long> findRecommendationsByUser(Long userID);
}
