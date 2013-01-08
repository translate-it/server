package com.amadeus.ori.translate.repository;

import com.amadeus.ori.translate.domain.Language;

public interface LanguageRepository extends IRepository<Language> {
	
	/**
	 * only called once to put all the 
	 * languages into the store
	 */
	public void initializeDataStore() ;

}
