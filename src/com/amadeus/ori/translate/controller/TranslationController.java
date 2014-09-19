package com.amadeus.ori.translate.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.domain.dto.KeywordTranslationDTO;
import com.amadeus.ori.translate.json.AdminResultMessage;
import com.amadeus.ori.translate.repository.KeywordRepository;
import com.amadeus.ori.translate.repository.TranslationRepository;
import com.amadeus.ori.translate.security.IdentityHolder;

/**
 * Is controlling all translation actions.
 * 
 * @author tobias.rothe@amadeus.com
 */
public class TranslationController implements Controller {

	private static final Log LOG = LogFactory
			.getLog(TranslationController.class);

	private KeywordRepository keywordRepository;
	
	private TranslationRepository translationRepository;

	private IdentityHolder identityHolder;

	/*
	 * Controller is invoked by the Spring Dispatcher. Configuration done in:
	 * /WebContent/WEB-INF/web.xml /WebContent/WEB-INF/Provisioner-servlet.xml
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");
		
		//LOG.debug(action);
		LOG.debug(action);	

		if (action.equals("list")) {
			return list(request.getParameter("projectId"), request.getParameter("language"), request.getParameter("source"),request.getParameter("index"));				
		
		} else if (action.equals("askGoogle")) {
		
			String query = URLEncoder.encode(request.getParameter("query"),"UTF-8");
			String sourceLanguage = request.getParameter("source");
			String language = request.getParameter("language");
			
			if (StringUtils.isEmpty(query)) {
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
			}
			
			URL url = new URL("https://www.googleapis.com/language/translate/v2?key=AIzaSyA8UJW74Xat4cClf3aybRg9mren8pa5p3c&q="+query+"&source="+sourceLanguage+"&target="+language+"&prettyprint=false");
			
			response.setContentType("application/json; charset=UTF-8");
			
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
            	sb.append(line);            
            }

            response.getWriter().write(sb.toString());
            LOG.debug(sb.toString());
            reader.close();
            response.flushBuffer();

			return null;				
			
		} else if (action.equals("addKeyword")) {
			
			String projectId = request.getParameter("projectId");
			String bundleId = request.getParameter("bundleId");
			String keyValue = request.getParameter("keyValue");
			String context = request.getParameter("context");
			String defaultTranslation = request.getParameter("defaultTranslation");
			
			if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(bundleId) || StringUtils.isEmpty(keyValue)) {
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
			}
			
			return addKeyword(projectId, bundleId, keyValue, context, defaultTranslation);

		}  else if (action.equals("deleteKeyword")) {

			String keywordId = request.getParameter("keywordId");
			return deleteKeyword(keywordId);
		
		}  else if (action.equals("setTranslation")) {
			
			String keywordId = request.getParameter("keywordId");
			String value = request.getParameter("value");
			String language = request.getParameter("language");
			String validated = request.getParameter("validated");
			
			if (StringUtils.isEmpty(keywordId)||StringUtils.isEmpty(language)) {
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
			}
			
			return setTranslation(keywordId, value, language, validated);
		} else if (action.equals("initialize")) {
				return new ModelAndView("jsonView", "msg", "done");

		}

		throw new ControllerException(Messages.ERROR_INVALID_REQUEST);

	}

//	private ModelAndView batchProcess(String cursorStr) {
//
//		LOG.debug("batch process translations, cursor:" + cursorStr);
//
//		TaskOptions taskOptions;
//		
//		Cursor cursor = translationRepository.batchProcess(cursorStr);
//		
//		if (cursor != null) {
//			//not done so we put a task queue entry to continue
//			taskOptions = TaskOptions.Builder.withUrl("/process/translate/batchProcess").param("cursor", cursor.toWebSafeString()).method(Method.GET);
//			//enqueue request
//			Queue queue = QueueFactory.getDefaultQueue();
//				  queue.add(taskOptions); 
//
//			 return new ModelAndView("jsonView","result", "NOT DONE, another request is put on the queue! cursor=" + cursor);			
//		}
//
//		return new ModelAndView("jsonView","result", "DONE!");	
//		
//	}

	private ModelAndView setTranslation(String keywordId, String value,
			String language, String val) {
		
		Translation translation = null;
		
		if (StringUtils.isEmpty(value)) {
			//this means that we remove the translation
			translationRepository.deleteByKey(keywordId + "." + language);
			
		} else {
		
			translation = new Translation(keywordId, language, value);
			if (val.equals("true")) {
				translation.setValidated(true);
			}
			
			//save or update this translation
			translation = translationRepository.save(translation);
		}
		
		return new ModelAndView("jsonView", "translation", translation);
		
	}

	private ModelAndView deleteKeyword(String keywordId) throws ControllerException {
		
		Keyword keyword = keywordRepository.findByKey(keywordId);
		
		if (keyword == null) {
			throw new ControllerException("A keyword with that name does not exists!");
		}
		
		//first we need to delete all translations
		long count = translationRepository.deleteForKeyword(keyword);
		LOG.debug("DELETE KEYWORD " + keywordId);
		LOG.debug(count + " translations have been deleted");

		keywordRepository.deleteByKey(keywordId);
		
		AdminResultMessage res = new AdminResultMessage();
		res.setMessage("Keyword " + keyword.getKeyValue() + " and " + count + " translations have been deleted");		
				
		return new ModelAndView("jsonView", "adminResult", res);
	}	
	
	private ModelAndView addKeyword(String projectId, String bundleId, String keyValue, String context, String defaultTranslation) throws ControllerException {
		
		if (keywordRepository.containsKeyword(bundleId, keyValue)) {
			throw new ControllerException("A keyword with that name already exists!");
		}
		
		Keyword keyword = new Keyword(Long.parseLong(projectId), bundleId, keyValue);
		keyword.setContext(context);
		keyword = keywordRepository.save(keyword);
		
		//save the default translation
		//TODO make language for default translation defineable
		Translation t = new Translation(keyword.getId(),"en",defaultTranslation);
		translationRepository.save(t);
				
		return new ModelAndView("jsonView", "keyword", keyword);
	}
	
	private ModelAndView list(String projectId, String language, String sourceLanguage, String index) {
		
		Collection<Keyword> keywords;
		if (index != null) {
			int i = Integer.parseInt(index);
			 keywords = keywordRepository.list(projectId, i, i + 100);
		} else{
			keywords = keywordRepository.list(projectId);
		}

		if ((keywords == null)||(keywords.size() < 1)) {
			//no more items found
			return new ModelAndView("jsonView", "keywordTranslation", null);
		}

		
		Map<String,KeywordTranslationDTO> results = new HashMap<String,KeywordTranslationDTO>();

		//////////////////////////
		// FILL map with keywords
		//////////////////////////
		
		for (Keyword keyword : keywords) {
			
			results.put(keyword.getId(), new KeywordTranslationDTO(keyword));
		}		
		
		///////////////////////////
		// ADD source translations
		///////////////////////////
			if (StringUtils.isEmpty(sourceLanguage)) {
				sourceLanguage = "en"; //default source language
			}
	
			boolean sameLanguage = sourceLanguage.equals(language); 
			Collection<Translation> sourceTranslations = translationRepository.list(sourceLanguage, projectId);
			if ((sourceTranslations != null)&&(sourceTranslations.size() > 0)) {
				for (Translation translation : sourceTranslations) {
					
						results.get(translation.getKeywordId()).setSourceTranslation(translation, sameLanguage);	
										
				}

			}

		///////////////////////////
		// ADD translations
		///////////////////////////			
			if (!sameLanguage) {

				Collection<Translation> translations = translationRepository.list(language, projectId);
				
				if ((translations != null)&&(translations.size() > 0)) {
					for (Translation translation : translations) {
						if (results.containsKey(translation.getKeywordId())) {
							results.get(translation.getKeywordId()).setTranslation(translation);
						}						
					}												
				}
			}

		LOG.debug("list done");	
		return new ModelAndView("jsonView", "keywordTranslation", results.values());
	}

	public void setIdentityHolder(IdentityHolder identityHolder) {
		this.identityHolder = identityHolder;
	}

	public void setKeywordRepository(KeywordRepository keywordRepository) {
		this.keywordRepository = keywordRepository;
	}

	public void setTranslationRepository(TranslationRepository translationRepository) {
		this.translationRepository = translationRepository;
	}

}
