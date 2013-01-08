package com.amadeus.ori.translate.repository.impl;

import java.util.Collection;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.repository.CacheManager;
import com.amadeus.ori.translate.repository.IRepository;
import com.amadeus.ori.translate.repository.KeywordRepository;
import com.amadeus.ori.translate.repository.ProjectRepository;
import com.amadeus.ori.translate.repository.TranslationRepository;


public class CacheManagerImpl implements CacheManager {
	
	private enum Prefix{TRANSL, KEYW, KEYW_LIST,PROJE, PROJE_LIST};

	private static final Log LOG = LogFactory.getLog(CacheManagerImpl.class);

	private static Cache cache;

	static {
		try {
			cache = javax.cache.CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
		} catch (CacheException e) {
			LOG.error("Error while initializing the cache.");
		}
	}



	public void clearAll() {
		LOG.info("Clearing the Cache...");
		cache.clear();
	}



	public Translation getOrPutTranslation(Keyword keyword, String language,
			TranslationRepository translationRepository) {		
		
		return (Translation) getOrPut(Prefix.TRANSL, keyword.getId() + "." + language,translationRepository);
	}


	@SuppressWarnings("unchecked")
	public Collection<Keyword> getOrPutKeywordList(KeywordRepository keywordRepository) {
		Object res = getOrPutList(Prefix.KEYW_LIST, keywordRepository);
		if (res != null) {
			return (Collection<Keyword>) res;
		}
		return null;
	}	
	


	@SuppressWarnings("unchecked")
	public Collection<Project> getOrPutProjectList(ProjectRepository projectRepository) {
		
		Object res = getOrPutList(Prefix.PROJE_LIST, projectRepository);
		if (res != null) {
			return (Collection<Project>) res;
		}
		return null;
	}



	@SuppressWarnings("unchecked")
	public Project putProject(Project b, ProjectRepository projectRepository) {
		
		Project project = projectRepository.save(b);
		cache.put(Prefix.PROJE + b.getId().toString(), b);
		cache.remove(Prefix.PROJE_LIST);

		return project;
	}



	@SuppressWarnings("unchecked")
	public Keyword putKeyword(Keyword k, KeywordRepository keywordRepository) {

		Keyword keyword = keywordRepository.save(k);
		cache.put(Prefix.KEYW + k.getId(), k);
		cache.remove(Prefix.KEYW_LIST);

		return keyword;
	}



	@SuppressWarnings("unchecked")
	public Translation putTranslation(Translation t, TranslationRepository translationRepository) {
		
		Translation transl = translationRepository.save(t);
		cache.put(Prefix.TRANSL + t.getId(), transl);
		
		return transl;
	}	
	

	
	
	@SuppressWarnings("unchecked")
	private Object getOrPut(Prefix prefix, String key, IRepository<?> repository){

		Object res = cache.get(prefix + key); 
		
		if (res != null) {
			//LOG.debug("cache hit for:" + prefix + key);
			return res;		
		}
		
		res = repository.findByKey(key);	
		
		if (res != null) {
			//LOG.debug("PUT for:" + prefix + key);
			cache.put(prefix + key, res);		
		}
		
		return res;		
	}

	@SuppressWarnings("unchecked")
	private Object getOrPutList(Prefix prefix, IRepository<?> repository){

		Object res = cache.get(prefix); 
		
		if (res != null) {
		//	LOG.debug("HIT for:" + prefix );
			return res;		
		}
		
		res = repository.list();
		
		if (res != null) {
		//	LOG.debug("PUT for:" + prefix );
			cache.put(prefix, res);		
		}
		
		return res;		
	}




	
}
