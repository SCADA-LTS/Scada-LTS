package org.scada_lts.dao;

import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

/**
 * Simple CRUD Interface
 *
 * This interface provide basic methods for entity management
 * using simple CRUD principle:
 * C - create
 * R - read
 * U - update
 * D - delete
 * Try to avoid returning void from the methods. Returning objects
 * helps with management of business objects within application.
 *
 * It is updated version of GenericDAO<T> interface developed
 * by Grzegorz Bylica.
 *
 * @author Radoslaw Jajko
 * @param <T> - Object to operate
 */
public interface CrudOperations<T> {

    int DAO_EMPTY_RESULT = 0;
    int DAO_EXCEPTION = -1;

    /**
     * Create object
     * Try to return the Object if it is possible.
     * It helps to maintain the REST API clear.
     *
     * @param entity Object to create
     * @return Operation result
     */
    public T create(T entity);

    /**
     * Get List of ScadaObjects
     * ScadaObjectIdentifier contains a brief information
     * about specific object that can be used in many places
     * to reduce the load of the Database. It contains
     * object name, id and exportId that are required to
     * identify that within application.
     * @return List of ScadaObjectIdentifiers
     */
    public List<ScadaObjectIdentifier> getSimpleList();

    /**
     * Get List of Objects
     *
     * @return List of Objects
     */
    public List<T> getAll();

    /**
     * Get object by ID
     *
     * @param id - object identifier
     * @return Object
     */
    public T getById(int id) throws EmptyResultDataAccessException;

    /**
     * Update object
     * Try to return the Object if it is possible.
     * Use that method for updating the object properties
     *
     * @param entity Object to create
     * @return Operation result
     */
    public T update(T entity);

    /**
     * Delete object
     *
     * @param id - object identifier
     * @return Operation result
     */
    public int delete(int id);

}
