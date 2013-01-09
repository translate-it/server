package com.amadeus.ori.translate.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.repository.LanguageRepository;

@ExporterName(name="java", description="Java property files")
public class JavaExporter implements Exporter {

    @Override
	public String getFilename(String bundleName, String language) {
		
		String filename;
		
		if (language.equals("en")) {
			filename = bundleName + "/" + bundleName;
		} else {
			filename = bundleName + "/" + bundleName + "_" + language;
		}		

		return filename;
	}

	@Override
	public void writeBundle(OutputStream outputStream, String bundleName,
			String language, Collection<Translation> translations)
			throws IOException {

		if ((translations != null)&&(translations.size() > 0)) {
			
			final StringBuilder sb = new StringBuilder();
			boolean first = true;

			for (Translation translation : translations) {
				String bundleId = translation.getBundle();
				
				if (bundleName.equals(bundleId)) {
					if (first) {
						first = false;
					} else {
						sb.append('\n');
					}

					sb.append(StringUtils.substringAfterLast(translation.getKeywordId(), "."));
					sb.append('=');
					sb.append(translation.getValue());
				}	
			}
			
			outputStream.write(sb.toString().getBytes("UTF-8"));
		}		
	}

	@Override
	public String getIndexFilename(String bundleName) {
		return bundleName + "/languages";		
	}

	/**
	 * Put a list with all the languages and their label into the file
	 */
	@Override
	public void writeIndex(OutputStream outputStream, String bundleName,
			String[] languages, LanguageRepository languageRepository) throws IOException {

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (String language : languages) {

			if (first) {
				first = false;
			} else {
				sb.append('\n');
			}

			sb.append(language);
			sb.append('=');
			sb.append(languageRepository.findByKey(language).getLabel());
		}

		outputStream.write(sb.toString().getBytes("UTF-8"));
	}
}
