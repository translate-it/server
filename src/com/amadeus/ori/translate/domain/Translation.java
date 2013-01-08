package com.amadeus.ori.translate.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Translation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Index
	private String keywordId;

	@Index
	private Long projectId;
	
	private Text value;

	@Index
	private String language;

	private Boolean validated;

	public Translation() {
		// for objectify
	}
	
	public Translation(String keywordId, String language, String value) {
		this.id = keywordId + "." + language;
		this.projectId = Long.parseLong(StringUtils.substringBefore(keywordId, "."));
		this.keywordId = keywordId;
		this.language = language;
		if (value != null) {
			this.value = new Text(value);	
		}		
		this.validated = false;

	}

	public String getValue() {

		if (value != null) {
			
			return	StringUtils.chomp(StringUtils.replaceChars(value.getValue(), '"', '\''));
		}
		return null;

	}

	public void setValue(String value) {
		
		if (value != null) {
			this.value = new Text(value);	
		}	
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getId() {
		return id;
	}

	public String getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(String keywordId) {
		this.keywordId = keywordId;
	}

	public String getBundle() {
		return StringUtils.substringBetween(keywordId, ".");
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}