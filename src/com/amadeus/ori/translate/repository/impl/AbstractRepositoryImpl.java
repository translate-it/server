package com.amadeus.ori.translate.repository.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import com.amadeus.ori.translate.repository.IRepository;
import com.amadeus.ori.translate.repository.exception.DAOException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public abstract class AbstractRepositoryImpl<T> implements IRepository<T> {

	private Class<T> persistentClass;

    
	@SuppressWarnings("unchecked")
	public AbstractRepositoryImpl() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T create(T entity) {

		return save(entity);
	}
 
	public T save(T entity) {

		Key<T> k = ofy().save().entity(entity).now();
		return ofy().load().key(k).get();
	}

	public void saveAll(Collection<T> entities) throws DAOException {

		ofy().save().entities(entities).now();
	}

	public void delete(Object entity) throws DAOException { 
		
		ofy().delete().entity(entity);
	}

	public void deleteByKey(String entityKey) throws DAOException {
		
		ofy().delete().type(persistentClass).id(entityKey).now();

	}
	
	public List<T> list() throws DAOException {
		
		return ofy().load().type(persistentClass).list();			 
	}
 
	public Collection<T> list(int startIndex, int endIndex) { 
			
			return ofy().load().type(persistentClass).offset(startIndex).limit(endIndex).list();	 
	}

	public boolean containsKey(String key) {
		
		return findByKey(key) != null;
	}
	
	public T findByKey(String key) {

		return ofy().load().type(persistentClass).id(key).get();
	}
	
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }


}
