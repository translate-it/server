package com.amadeus.ori.translate.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.json.AdminResultMessage;
import com.amadeus.ori.translate.repository.ProjectRepository;
import com.amadeus.ori.translate.security.IdentityHolder;


/**
 * 
 * @author ingolf.tobias.rothe@amadeus.com
 */
public class ProjectController implements Controller {

	private static final Log LOG = LogFactory.getLog(ProjectController.class);

	private ProjectRepository projectRepository; 
	
	private IdentityHolder identityHolder;
	 
	/*
	 * Controller is invoked by the Spring Dispatcher. Configuration done in:
	 * /WebContent/WEB-INF/web.xml /WebContent/WEB-INF/Provisioner-servlet.xml
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");

		LOG.debug(action);

		if (action.equals("listProjects")) {

			return new ModelAndView("jsonView", "project", projectRepository.list());
			
		} else if (action.equals("createProject")) {
			
			final String projectname = request.getParameter("name"); 
			final String description = request.getParameter("description");
	
			return createProject(projectname, description);
		}
		
		/*
		 * FOLLOWING ACTIONS ARE ONLY FOR ADMIN USERS
		 */
		if ((identityHolder == null)||(!identityHolder.isAdmin())) {
			throw new ControllerException(Messages.UNAUTHORIZED);
		}
		
		final String projectid = request.getParameter("id");
		if (StringUtils.isEmpty(projectid) ) {				
			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);				
		} 
			
		if (action.equals("getProject")) {
 
			return new ModelAndView("jsonView", "project", projectRepository.findByKey(projectid));

		} else if (action.equals("updateProject")) {
			
			final String projectname = request.getParameter("name");			
			final String description = request.getParameter("description");
			final String bundles = request.getParameter("bundles");
			final String languages = request.getParameter("languages");
			final String exportLanguages = request.getParameter("exportLanguages");			 
		
			return updateProject(projectid, projectname, description, bundles, languages, exportLanguages);								


		} else if (action.equals("deleteProject")) {
			
			return deleteProject(projectid);								

		} else {

			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		}

	}


	private ModelAndView createProject(String projectname, String description) throws Exception {
		
		Project a = new Project(projectname);
			 	a.setDescription(description);
			 	a.setLanguages("en,fr,de,es");
			 	a.setExportLanguages("en,fr,de,es");
			 	a.setBundles("default");

		
		Project res = projectRepository.save(a);

		return new ModelAndView("jsonView", "project", res);
	}

	
	private ModelAndView updateProject(String projectid, String projectname, String description,
			String bundles, String languages, String exportLanguages) throws ControllerException{
		
		 
		
		Project project = projectRepository.findByKey(projectid);
		
		//check if project exists
		if (project == null) {
			throw new ControllerException("Project not found");
		} 
		
		project.setName(projectname);
		project.setDescription(description);
		project.setBundles(bundles);
		project.setLanguages(languages);
		project.setExportLanguages(exportLanguages); 
				
		projectRepository.save(project);
		
		return new ModelAndView("jsonView", "project", project);
		
	}

	private ModelAndView deleteProject(String projectid) throws ControllerException{

		projectRepository.deleteByKey(projectid);
		
		AdminResultMessage res = new AdminResultMessage();
						   res.setMessage("Project deleted.");

		return new ModelAndView("jsonView", "adminResult", res);
	}


	

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}


	public void setIdentityHolder(IdentityHolder identityHolder) {
		this.identityHolder = identityHolder;
	}


}
