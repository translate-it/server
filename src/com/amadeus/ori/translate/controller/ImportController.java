package com.amadeus.ori.translate.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;
import com.amadeus.ori.translate.importers.Importer;
import com.amadeus.ori.translate.importers.ImporterFactory;
import com.amadeus.ori.translate.json.AdminResultMessage;
import com.amadeus.ori.translate.repository.KeywordRepository;
import com.amadeus.ori.translate.repository.LogEntryRepository;
import com.amadeus.ori.translate.repository.TranslationRepository;
import com.amadeus.ori.translate.security.IdentityHolder;

/**
 * 
 * @author ingolf.tobias.rothe@amadeus.com
 */
public class ImportController implements Controller {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(ImportController.class);

	private TranslationRepository translationRepository;

	private KeywordRepository keywordRepository;

	private IdentityHolder identityHolder;

	private LogEntryRepository logEntryRepository;

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

		// if ((identityHolder == null)||(!identityHolder.isAdmin())) {
		// throw new ControllerException(Messages.UNAUTHORIZED);
		// }

		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");

		LOG.debug(action);

		if (action.equals("loadFile")) {

			String formatId = request.getParameter("formatId");
			String delimiter = request.getParameter("delimiter");
			if (StringUtils.isEmpty(delimiter)) {
				delimiter = "=";
			}

			if (StringUtils.isEmpty(formatId)) {
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
			}

			return importTranslation(formatId, delimiter, request);

		} else if (action.equals("addTranslation")) {

			String keyValue = request.getParameter("keyValue");
			String translationValue = request.getParameter("translationValue");
			String language = request.getParameter("language");
			String bundleId = request.getParameter("bundle");
			String projectId = request.getParameter("projectId");

			if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(bundleId)
					|| StringUtils.isEmpty(keyValue)) {
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
			}

			return addTranslation(keyValue, translationValue, language,
					bundleId, projectId);

		} else if (action.equals("listFormats")) {
			return listFormats();
		} else {
			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		}
	}

	private ModelAndView listFormats() {
		return new ModelAndView("jsonView", "formats",
				ImporterFactory.listFormats());
	}

	private ModelAndView addTranslation(String keyValue,
			String translationValue, String language, String bundle,
			String projectId) {

		keyValue = StringUtils.deleteWhitespace(keyValue);

		String keywordId = projectId + "." + bundle + "." + keyValue;

		// create the keyword if it does not exists so far
		if (!keywordRepository.containsKey(keywordId)) {
			Keyword keyword = new Keyword(Long.parseLong(projectId), bundle,
					keyValue);
			keywordRepository.save(keyword);
		}

		Translation t = new Translation(keywordId, language, translationValue);
		translationRepository.save(t);
		AdminResultMessage msg = new AdminResultMessage();
		msg.setMessage("done");
		return new ModelAndView("jsonView", "adminResult", msg);
	}

	private ModelAndView importTranslation(String formatId, String delimiter,
			HttpServletRequest request) throws ControllerException {
		LOG.debug("loading file ... ");

		// Get the file format importer
		final Importer importer = ImporterFactory.get(formatId);

		if (importer == null) {
			throw new ControllerException("Unknown import format: " + formatId);
		}

		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();

				if (!item.isFormField()) {

					LOG.debug("Got an uploaded file: " + item.getFieldName()
							+ ", name = " + item.getName() + ", format = "
							+ formatId);

					List<TranslationImportDTO> result = importer
							.importFromStream(item.openStream());

					return new ModelAndView("jsonView", "contents", result);
				}
			}
		} catch (Exception ex) {
			LOG.warn("File upload failed", ex);
			throw new ControllerException("File upload failed");
		}

		return null;
	}

	public void setTranslationRepository(
			TranslationRepository translationRepository) {
		this.translationRepository = translationRepository;
	}

	public void setIdentityHolder(IdentityHolder identityHolder) {
		this.identityHolder = identityHolder;
	}

	public void setLogEntryRepository(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}

	public void setKeywordRepository(KeywordRepository keywordRepository) {
		this.keywordRepository = keywordRepository;
	}
}