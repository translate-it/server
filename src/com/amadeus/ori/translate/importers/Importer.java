package com.amadeus.ori.translate.importers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;

public interface Importer {
	List<TranslationImportDTO> importFromStream(InputStream is) throws IOException;
}
