package com.amadeus.ori.translate.importers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amadeus.ori.translate.domain.dto.TranslationImportDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ImporterName(name="json", description="JSON object")
public class JSONImporter implements Importer {

	@Override
	public List<TranslationImportDTO> importFromStream(InputStream is)
			throws IOException {

		// Parse the incoming JSON
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> translationPairs = mapper.readValue(is, new TypeReference<HashMap<String,String>>(){});
		
		List<TranslationImportDTO> results = new ArrayList<TranslationImportDTO>();
		
		for(Map.Entry<String, String> entry : translationPairs.entrySet()) {
		    String key = entry.getKey();
		    String translation = entry.getValue();

			TranslationImportDTO item = new TranslationImportDTO(key, translation);
			if (item.hasKey()) {
				results.add(item);
			} else {
				throw new IOException("Invalid format: key=\"" + key + "\", value=\"" + translation + "\"");
			}
		}
		
		return results;
	}

}
