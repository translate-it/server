package com.amadeus.ori.translate.importers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;

@ImporterName(name = "android", description = "Android values XML file")
public class AndroidImporter implements Importer {

	private static final Log LOG = LogFactory.getLog(AndroidImporter.class);

	@Override
	public List<TranslationImportDTO> importFromStream(InputStream is)
			throws IOException {

		// Output translations list
		List<TranslationImportDTO> results = null;

		// Parse the incoming XML
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader;

		try {
			reader = factory.createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			LOG.warn("Unable to read XML stream", e);
			throw new IOException(e);
		}
		try {
			while (reader.hasNext()) {
				reader.next();
				int event = reader.getEventType();

				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					String name = reader.getLocalName();
					if ("resources".equals(name)) {
						results = parseResources(reader);
					} else {
						LOG.warn("Unexpected element: " + name);
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			LOG.warn("Unable to parse XML", e);
			throw new IOException(e);
		} catch (Exception e) {
			LOG.warn("Unable to parse XML", e);
			throw new IOException(e);
		} finally {
			try {
				reader.close();
			} catch (XMLStreamException e) {
				LOG.warn("Unable to close XML stream", e);
			}
		}

		return results;
	}

	private List<TranslationImportDTO> parseResources(XMLStreamReader reader)
			throws Exception {
		List<TranslationImportDTO> results = new ArrayList<TranslationImportDTO>();

		while (reader.hasNext()) {
			reader.next();
			int event = reader.getEventType();

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				String name = reader.getLocalName();
				if ("string".equals(name)) {
					TranslationImportDTO translation = parseString(reader);
					if (translation != null)
						results.add(translation);
				} else {
					LOG.warn("Unexpected element: " + name);
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				String endEltName = reader.getLocalName();
				if ("resources".equals(endEltName)) {
					return results;
				} else {
					LOG.warn("Unexpected end element: " + endEltName);
				}

				break;
			}
		}

		return results;
	}

	private TranslationImportDTO parseString(XMLStreamReader reader)
			throws Exception {

		String key = reader.getAttributeValue(null, "name");
		String translation = reader.getElementText();
		if (StringUtils.contains(translation, '\\')) {
			translation = translation.replace("\\\"", "\"").replace("\\'", "'");
		}

		// Store the translation
		TranslationImportDTO item = new TranslationImportDTO(key, translation);

		if (item.hasKey()) {
			return item;
		} else {
			throw new Exception("Invalid format: key=\"" + key + "\", value=\""
					+ translation + "\"");
		}

	}

}
