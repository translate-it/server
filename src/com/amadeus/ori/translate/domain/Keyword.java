
package com.amadeus.ori.translate.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Keyword implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id   
	private String id;

	private String keyValue;   

	private String bundle;  

	@Index
	private Long projectId;    

	private String context;

	public Keyword() {
		// for objectify
	}

	public Keyword(Long projectId, String bundleId, String keyValue) {

		this.id = projectId + "." + bundleId + "." + keyValue;
		this.projectId = projectId;
		this.bundle = bundleId;
		this.keyValue = StringUtils.remove(keyValue, ".");
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getId() {
		return id;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}    

}
