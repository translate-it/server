package com.amadeus.ori.translate.importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;

@ImporterName(name="csv", description="Comma-separated values files")
public class CsvImporter implements Importer {

	@Override
	public List<TranslationImportDTO> importFromStream(InputStream is)
			throws IOException {

		//TODO: add parameter
		String delimiter = "=";
		
		return importFromStream(is, delimiter);
	}
	
	protected List<TranslationImportDTO> importFromStream(InputStream is, String delimiter)
			throws IOException {
		
		final BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		List<TranslationImportDTO> results = new ArrayList<TranslationImportDTO>();
		
		String line;	
		
		while ((line = r.readLine()) != null) {				
						
			line = StringUtils.chomp(line); //remove tailing line breaks
			TranslationImportDTO item = new TranslationImportDTO(line.split(delimiter));
			
			if (item.hasKey()) {
				results.add(item);
			}
		}
		
		return results;
	}
}
