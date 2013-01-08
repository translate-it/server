package com.amadeus.ori.translate.domain.dto;

import org.apache.commons.lang.StringUtils;

public class TranslationImportDTO {

	private String keyValue;
	private String translationValue;

	public TranslationImportDTO(String key, String translation) {
		keyValue = key;
		translationValue = translation;
	}
	
	public TranslationImportDTO(String[] tokenArray) {

		if (tokenArray.length > 0) {
			keyValue = tokenArray[0];
		}
		if (tokenArray.length > 1) {
			translationValue = tokenArray[1];
		}

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

	public boolean hasKey() {		
	
		return(!StringUtils.isEmpty(keyValue) && keyValue.length() > 1);
	}

}
