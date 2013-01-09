package com.amadeus.ori.translate.importers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;

/**
 * Imports Java property files
 * @author bbezine
 *
 */
@ImporterName(name="java", description="Java property files")
public class JavaImporter extends CsvImporter {
	@Override
	public List<TranslationImportDTO> importFromStream(InputStream is)
			throws IOException {

		return importFromStream(is, "=");
	}
}
