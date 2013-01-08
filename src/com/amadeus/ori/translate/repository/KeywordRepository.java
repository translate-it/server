package com.amadeus.ori.translate.repository;

import java.util.Collection;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.repository.exception.DAOException;

public interface KeywordRepository extends IRepository<Keyword> {

	Collection<Keyword> list(String projectId) throws DAOException;
	
	Collection<Keyword> list(String projectId, int startIndex, int endIndex);
	
	boolean containsKeyword(String bundleId, String keyValue);

}
