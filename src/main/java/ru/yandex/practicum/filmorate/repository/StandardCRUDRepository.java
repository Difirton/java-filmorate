package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

/**
 * An interface that defines a basic set of data access operations.
 * This interface is designed to inherit the definition of the type
 * of the returned object in the descendant
 *
 * @author Dmitriy Kruglov
 */
public interface StandardCRUDRepository<M> {

    /**
     * Accepts an object to save in the database.
     * @param m the object to save in the database
     * @return the object that was passed to the method
     * after insert in field identifier.
     */
    M save(M m);

    /**
     * Accepts an object to update it in the database.
     * @param m the object to update in the database
     * @return the object with the identifier set if it was not present
     * when the object was passed to the method
     * @apiNote the object is returned unchanged,
     * the information in the database is changed without checking
     * for the presence of a tuple in the database
     */
    M update(M m);

    /**
     * Accepts an object identifier in the database and removes
     * the database tuple associated with the object id.
     * @param id object identifier to delete from the database
     * @return the number of deleted rows in a table
     */
    int deleteById(Long id);

    /**
     * Returns a List of all objects stored in the database
     * @return a List of objects that match the specified element type
     */
    List<M> findAll();

    /**
     * Accepts an object identifier in the database and return
     * not empty {@link Optional} of object if it exists.
     * @param id identifier of object in the database
     * @return {@link Optional} the single mapped object
     * ({@link Optional} can not be {@code null}
     * if the given {@code id} is incorrect returned empty {@link Optional})
     */
    Optional<M> findById(Long id);

    /**
     * Accepts a list of objects that match the specified element type
     * and stores all objects from the list in the database
     * @param m a List of objects that match the specified element type
     * @return an array containing the numbers of rows affected by each update in the batch
     */
    int[] saveAll(List<M> m);
}
