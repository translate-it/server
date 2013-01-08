package com.amadeus.ori.translate.domain;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Language code and name
 * 
 * @author tobias.rothe@amadeus.com
 */
@Entity
public class Language implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String name;

	private String label;
	
	public Language() {
		// for objectify
	}
	
	public Language(String id, String name, String label) {
		this.id = id;
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		if (label == "") {
			return name;
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
