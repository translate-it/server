package com.amadeus.ori.translate.repository.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amadeus.ori.translate.domain.Language;
import com.amadeus.ori.translate.repository.LanguageRepository;

public class LanguageRepositoryImpl extends AbstractRepositoryImpl<Language>
		implements LanguageRepository {
	
	private static final String LANGUAGES = "languages.csv";

    static {
    	factory().register(Language.class); 
    }
    
	public void initializeDataStore() {
		
		List<Language> list = new ArrayList<Language>();
		
		try {

			final BufferedReader r = new BufferedReader(new InputStreamReader(LanguageRepositoryImpl.class.getClassLoader().getResourceAsStream(LANGUAGES),"UTF-8"));

			String[] tokenArray = null;
			String line;
			
			while ((line = r.readLine()) != null) {				
							
				tokenArray = line.split(",");	
				

				if (tokenArray.length == 2 ) {
					list.add(new Language(tokenArray[0].trim(),tokenArray[1].trim(),tokenArray[1].trim()));
				}else if (tokenArray.length == 3 ) {
					list.add(new Language(tokenArray[0].trim(),tokenArray[1].trim(),tokenArray[2].trim()));
				}
			}
			


		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		
		saveAll(list);
	}


}
