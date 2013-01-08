package com.amadeus.ori.translate.repository.impl;

import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.repository.ProjectRepository;

public class ProjectRepositoryImpl extends AbstractRepositoryImpl<Project> implements ProjectRepository {

    static {
    	factory().register(Project.class); 
    }
    

	public Project findByKey(String key) {

		return ofy().load().type(Project.class).id(Long.parseLong(key)).get();
	}
}
