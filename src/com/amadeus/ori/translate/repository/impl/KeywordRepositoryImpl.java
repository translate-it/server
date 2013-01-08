package com.amadeus.ori.translate.repository.impl;

import java.util.Collection;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.repository.KeywordRepository;
import com.amadeus.ori.translate.repository.exception.DAOException;

public class KeywordRepositoryImpl extends AbstractRepositoryImpl<Keyword> implements KeywordRepository {

    static {
    	factory().register(Keyword.class); 
    }
    
	public boolean containsKeyword(String bundleId, String keyValue) {
		return containsKey(bundleId + "." + keyValue);
	}

	@Override
	public Collection<Keyword> list(String projectId) throws DAOException {
		return ofy().load().type(Keyword.class).filter("projectId == ", Long.parseLong(projectId)).list();
	}

	@Override
	public Collection<Keyword> list(String projectId, int startIndex, int endIndex) {
		return ofy().load().type(Keyword.class).filter("projectId == ", Long.parseLong(projectId)).offset(startIndex).limit(endIndex).list();	
	}


}
