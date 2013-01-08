package com.amadeus.ori.translate.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.repository.LanguageRepository;

public interface Exporter {
	String getFilename(String bundle, String language);
	void writeBundle(OutputStream outputStream, String bundleName, String language, Collection<Translation> translations) throws IOException;
	
	String getIndexFilename(String bundleName);
	void writeIndex(OutputStream outputStream, String bundleName, String[] languages, LanguageRepository languageRepository) throws IOException;
}
