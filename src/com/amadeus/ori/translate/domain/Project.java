package com.amadeus.ori.translate.domain;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Project
 * 
 * @author tobias.rothe@amadeus.com
 */
@Entity
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String name;

	private String bundles;
	
	private String languages;

	private String exportLanguages;	
	
	private String description;
	
	private boolean enabled;


	public Project() {
		// for objectify
	}
	
	public Project(String projectname) {
		this.name = projectname;
		enabled = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBundles() {
		return bundles;
	}

	public void setBundles(String bundles) {
		this.bundles = bundles;
	}

	/**
	 * @return comma separated list of language codes
	 */
	public String getLanguages() {
		return languages;
	}

	/**
	 * @param languages comma separated list of language codes
	 */
	public void setLanguages(String languages) {
		this.languages = languages;
	}

	/**
	 * @return comma separated list of language codes
	 */
	public String getExportLanguages() {
		return exportLanguages;
	}

	/**
	 * @param exportLanguages comma separated list of language codes
	 */
	public void setExportLanguages(String exportLanguages) {
		this.exportLanguages = exportLanguages;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
