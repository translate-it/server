package com.amadeus.ori.translate.domain.dto;

import org.apache.commons.lang.StringUtils;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;

public class KeywordTranslationDTO {

	private String keyWordId;
	private String translationId;
	private String keyValue; 
	private String translationValue;
	private String context;
	private String bundle;
	private String sourceTranslation;

	public KeywordTranslationDTO(Keyword keyword) {
		this(keyword, null);
	}
	
	public KeywordTranslationDTO(Keyword keyword, Translation translation) {
		this.keyValue = keyword.getKeyValue();
		this.keyWordId = keyword.getId(); 
		this.context = keyword.getKeyValue();
		this.bundle = StringUtils.substringBetween(keyword.getId(),".",".");
		
		if (translation != null) {
			this.translationValue = translation.getValue();
			this.translationId = translation.getId();
		}		
	}


	public String getKeyWordId() {
		return keyWordId;
	}
	public void setKeyWordId(String keyWordId) {
		this.keyWordId = keyWordId;
	}
	public String getTranslationId() {
		return translationId;
	}
	public void setTranslationId(String translationId) {
		this.translationId = translationId;
	}
	public String getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	public String getTranslationValue() {
		return translationValue;
	}
	public void setTranslationValue(String translationValue) {
		this.translationValue = translationValue;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getBundle() {
		return bundle;
	}
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public void setSourceTranslation(Translation translation, boolean isTranslation) {
		if (translation != null) {
			this.sourceTranslation = translation.getValue();
			
			if (isTranslation) {
				this.translationValue = translation.getValue();
				this.translationId = translation.getId();				
			}
		} else {
			this.sourceTranslation = "";
		}	
	}

	public void setTranslation(Translation translation) {
		
		if (translation != null) {
			this.translationValue = translation.getValue();
			this.translationId = translation.getId();
		}		
	}	
	
	public String getSourceTranslation() {
		return sourceTranslation;
	}


}
