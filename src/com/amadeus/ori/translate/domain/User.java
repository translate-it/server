package com.amadeus.ori.translate.domain;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;


@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String username;

	private String password;

    private String firstName;
    
    private String lastName;
    
    private String email;
	
    private String languages;

	private String projects;
    
	private String bundles;

	private Role role;

	private boolean isEnabled;

	private boolean isGoogleAccount;

	public User() {
		// for objectify
	}
	
	public User(String username, String password, boolean isGoogleAccount) {

		this.username = username.toLowerCase();
		this.password = password;
		this.isGoogleAccount = isGoogleAccount;
		this.role = Role.ROLE_USER; //default role
	}
	
	/**
	 * For security reason there is no public get method for the password.
	 * The application needs to pass a SHA256 hashed password that  will
	 * be compared with the password stored in the user object.
	 * 
	 * @param passwd a SHA256 hashed password
	 * @return true if passwords match
	 */
	public boolean comparePassword(String passwd) {
		return getPassword().equals(passwd);
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}
	private String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBundles() {
		return bundles;
	}

	public void setBundles(String bundles) {
		this.bundles = bundles;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLanguages() {
		return languages;
	}
	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isGoogleAccount() {
		return isGoogleAccount;
	}

	public void setGoogleAccount(boolean isGoogleAccount) {
		this.isGoogleAccount = isGoogleAccount;
	}

	public String getProjects() {
		return projects;
	}

	public void setProjects(String projects) {
		this.projects = projects;
	}

}