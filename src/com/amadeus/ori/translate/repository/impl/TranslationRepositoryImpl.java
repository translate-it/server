package com.amadeus.ori.translate.repository.impl;

import java.util.Collection;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.repository.TranslationRepository;
import com.googlecode.objectify.cmd.QueryKeys;


public class TranslationRepositoryImpl extends AbstractRepositoryImpl<Translation> implements TranslationRepository {

	
    static {
    	factory().register(Translation.class); 
    }
    
	public long deleteForKeyword(Keyword keyword) {

		QueryKeys<Translation> allKeys = ofy().load().type(Translation.class).filter("keywordId == ", keyword.getId()).keys();
		int count = allKeys.list().size();
		// Useful for deleting items
		ofy().delete().keys(allKeys);
		
		return count;		
	}

	public Collection<Translation> list(String language, String projectId) {
		Long pid = Long.parseLong(projectId);
		return ofy().load().type(Translation.class).filter("language == ", language).filter("projectId == ", pid).list();
				
	}

//	@SuppressWarnings("unchecked")
//	public Cursor batchProcess(String cursorString) {
//
//
//		PersistenceManager pm = PMF.get().getPersistenceManager();
//		final int LIMIT = 20;
//		
//		Query query = pm.newQuery(Translation.class);
//		pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
//		pm.setDetachAllOnCommit(true);
//		
//	    if (cursorString == null){    	
//
//	        query.setRange(0, LIMIT);
//
//     
//	    } else {
//	        Cursor cursor = Cursor.fromWebSafeString(cursorString);
//	        Map<String, Object> extensionMap = new HashMap<String, Object>();
//	        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
//	        query.setExtensions(extensionMap);
//	        query.setRange(0, LIMIT);	    	
//	    }
//
//        List<Translation> translations = (List<Translation>) query.execute();
//        // Use the first 20 results...
//        
//        for (Translation translation : translations) {
//        	
//          if(translation.getValue().contains("{")){
//        	  
//        	LOG.debug("processing translation " + translation.getId() + " : " + translation.getValue());        	  
//            String value = translation.getValue();
//            	   value = StringUtils.replaceAll(value, "{0}", "%@");
//            	   value = StringUtils.replaceAll(value, "{1}", "%@");
//            	   value = StringUtils.replaceAll(value, "{2}", "%@");
//            	   
//			translation.setValue(value);
//
//			LOG.debug("                done:   " + translation.getId() + " : " + translation.getValue());        			
//
//			save(pm.detachCopy(translation));
////			pm.flush();
//          }
//		}
//        
//        
//        
//        if (translations.size() == LIMIT) {
//        	Cursor cursor = JDOCursorHelper.getCursor(translations);
//        	return cursor;
//		}
//        
//        return null;
//
//	}	
}