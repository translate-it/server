package com.amadeus.ori.translate.security;

import java.io.Serializable;

import com.amadeus.ori.translate.domain.Role;
import com.amadeus.ori.translate.domain.User;

/**
 * Holds the identity of the currently authenticated user.
 * To be stored in the session.
 *
 */
public class IdentityHolder  implements Serializable {

	private static final long serialVersionUID = 1L;
	private User identity;

	public User getIdentity() {
		return identity;
	}

	public void setIdentity(User identity) throws UserDisabledException {
		if ((identity != null)&&(!identity.isEnabled())) {
			//disabled users can not open a session
			throw new UserDisabledException();
		}
		this.identity = identity;
	}

	public void destroyIdentity() {
		this.identity = null;
		
	}

	public boolean hasIdentity() {
		return identity != null;
	}
	
	public boolean isAdmin(){
		return ((identity!=null)&&(identity.getRole() == Role.ROLE_ADMIN));
	}
}
