package com.amadeus.ori.translate.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.exporters.Exporter;
import com.amadeus.ori.translate.exporters.ExporterFactory;
import com.amadeus.ori.translate.repository.LanguageRepository;
import com.amadeus.ori.translate.repository.ProjectRepository;
import com.amadeus.ori.translate.repository.TranslationRepository;

/**
 * Is controlling all export actions.
 * 
 * @author tobias.rothe@amadeus.com
 */
public class ExportController implements Controller {

//	private static final Log LOG = LogFactory.getLog(ExportController.class);

	
	private TranslationRepository translationRepository;
	
	private ProjectRepository projectRepository; 	
	
	private LanguageRepository languageRepository;



	/*
	 * Controller is invoked by the Spring Dispatcher. Configuration done in:
	 * /WebContent/WEB-INF/web.xml /WebContent/WEB-INF/Provisioner-servlet.xml
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");
		
		// Get the project ID from the URL
		String projectId = request.getParameter("projectId");
		
		if (StringUtils.isEmpty(action)) {
			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		} else if (action.equals("listFormats")){
			return listFormats();
		} else if (StringUtils.isEmpty(projectId)) {
			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		}
		
		return exportZipped(response, action, projectId, request.getParameter("bundle"));		
	}

	private ModelAndView listFormats() {
		return new ModelAndView("jsonView", "formats", ExporterFactory.list());
	}
	
	private ModelAndView exportZipped(HttpServletResponse res, String exporterName,
			String projectId, String bundleNames) throws IOException, ControllerException {

		// Get the project
		final Project project = projectRepository.findByKey(projectId);

		if (project == null) {
			throw new ControllerException("Unknown project: " + projectId);
		}

		// Get the languages to export
		final String str = project.getExportLanguages();

		if (StringUtils.isEmpty(str)) {
			throw new ControllerException("No export languages defined for this project.");
		}
		
		// Get the file format exporter
		final Exporter exporter = ExporterFactory.get(exporterName);

		if (exporter == null) {
			throw new ControllerException("Unknown export format: " + exporterName);
		}

		//this has to be done once to load all languages to the datastore
		if (languageRepository.list().size() < 1) {
			languageRepository.initializeDataStore();
		}

		String[] languages = str.split(",");
		String packageName =  project.getName().toLowerCase() + "_" + exporterName.toLowerCase() + "_translations.zip";

		res.setContentType("application/zip");
		res.setHeader("Content-Disposition", "inline; filename=" + packageName + ";");
		res.setCharacterEncoding("UTF-8");

		ZipOutputStream zipOutStream = new ZipOutputStream(res.getOutputStream());

		Map<String, Collection<Translation>> tmpMap = new HashMap<String, Collection<Translation>>();


		String[] allBundles = project.getBundles().split(",");
		List<String> exportBundles = new ArrayList<String>();
		
		
		if (bundleNames != null) {
			// only export the specified bundles
			for (String bundle : allBundles) {
				if (StringUtils.contains(bundleNames, bundle)) {
					exportBundles.add(bundle);
				}
			}
		} else {
			// export all bundles			
			exportBundles.addAll(Arrays.asList(allBundles));
		}

		for (String bundle : exportBundles) {

			final String bundleName = bundle;
			
			for (String language : languages) {

				if (!tmpMap.containsKey(language)) {
					// only load that once
					tmpMap.put(language, translationRepository.list(language, projectId));
				}

				// Create a new entry (file) in the ZIP archive
				ZipEntry zipEntry = new ZipEntry(exporter.getFilename(bundleName, language));
				zipOutStream.putNextEntry(zipEntry);
				
				exporter.writeBundle(zipOutStream, bundle, language,
						tmpMap.get(language));
				
				// Close the entry in the ZIP archive
				zipOutStream.closeEntry();
			}

			// Write index if needed
			final String indexFilename = exporter.getIndexFilename(bundleName);
			if (indexFilename != null) {
				// Create a new entry (file) in the ZIP archive
				ZipEntry zipEntry = new ZipEntry(exporter.getIndexFilename(bundleName));
				zipOutStream.putNextEntry(zipEntry);

				exporter.writeIndex(zipOutStream, bundleName, languages, languageRepository);

				// Close the entry in the ZIP archive
				zipOutStream.closeEntry();
			}
		}

		zipOutStream.close();

		return null;
	}

	public void setTranslationRepository(TranslationRepository translationRepository) {
		this.translationRepository = translationRepository;
	}

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public void setLanguageRepository(LanguageRepository languageRepository) {
		this.languageRepository = languageRepository;
	}
}
