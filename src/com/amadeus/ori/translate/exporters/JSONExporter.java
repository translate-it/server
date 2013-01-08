package com.amadeus.ori.translate.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.repository.LanguageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExporterName(name="json")
public class JSONExporter implements Exporter {
	
	@Override
	public String getFilename(String bundle, String language) {
		return language + "/strings.js";
	}

	@Override
	public void writeBundle(OutputStream outputStream, String bundleName,
			String language, Collection<Translation> translations)
			throws IOException {
		
		if ((translations != null) && (translations.size() > 0)) {

			// Create the JSON object tree
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode translationsObj = mapper.createObjectNode();
			
			for (Translation translation : translations) {
				String bundleId = translation.getBundle();
				if (bundleName.equals(bundleId)) {

					String key = StringUtils.substringAfterLast(translation.getKeywordId(), ".");
					translationsObj.put(key, translation.getValue());
				}	
			}
			
			String output = mapper.writeValueAsString(translationsObj);
			outputStream.write(output.getBytes("UTF-8"));
		}
	}

	@Override
	public String getIndexFilename(String bundleName) {
		// No index in case of iOS resource files
		return null;
	}

	@Override
	public void writeIndex(OutputStream outputStream, String bundleName,
			String[] languages, LanguageRepository languageRepository)
			throws IOException {
		// No index in case of iOS resource files
		throw new NotImplementedException();
	}

}
