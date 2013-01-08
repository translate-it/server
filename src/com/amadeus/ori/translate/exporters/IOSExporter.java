package com.amadeus.ori.translate.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.amadeus.ori.translate.domain.Translation;
import com.amadeus.ori.translate.iphone.IOSLanguageCodeAdapter;
import com.amadeus.ori.translate.repository.LanguageRepository;

@ExporterName(name="ios")
public class IOSExporter implements Exporter {

	@Override
	public void writeBundle(OutputStream outputStream, String bundle,
			String language, Collection<Translation> translations)
			throws IOException {
		
		if ((translations != null) && (translations.size() > 0)) {
			
			StringBuilder sb = new StringBuilder();
			boolean first = true;

			for (Translation translation : translations) {
				String bundleId = translation.getBundle();
				if (bundle.equals(bundleId)) {
					if (first) {
						first = false;
					} else {
						sb.append('\n');
					}

					sb.append('"');
					sb.append(StringUtils.substringAfterLast(translation.getKeywordId(), "."));
					sb.append("\"=\"");
					sb.append(translation.getValue());
					sb.append("\";");
				}	
			}												

			outputStream.write(sb.toString().getBytes("UTF-16"));
		}
	}

	@Override
	public String getFilename(String bundle, String language) {
		return IOSLanguageCodeAdapter.adaptLanguageCode(language) + ".lproj" + "/Localizable.strings";
	}

	@Override
	public String getIndexFilename(String bundle) {
		// No index in case of iOS resource files
		return null;
	}

	@Override
	public void writeIndex(OutputStream outputStream, String bundle,
			String[] languages, LanguageRepository languageRepository)
			throws IOException {

		// No index in case of iOS resource files
		throw new NotImplementedException();
	}
}
