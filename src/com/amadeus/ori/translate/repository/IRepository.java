package com.amadeus.ori.translate.repository;

import java.util.Collection;
import java.util.List;

import com.amadeus.ori.translate.repository.exception.DAOException;

public interface IRepository<T> {

	T create(T entity) throws DAOException;
	
	T save(T entity) throws DAOException;
	
	void saveAll(Collection<T> entities) throws DAOException;

	void deleteByKey(String entityKey) throws DAOException;	
	
	void delete(Object entity) throws DAOException;

	List<T> list() throws DAOException;
	
	Collection<T> list(int startIndex, int endIndex);

	boolean containsKey(String key);
	
	T findByKey(String key);
	
}