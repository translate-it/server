package com.amadeus.ori.translate.repository;

import java.util.Collection;

import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;

public interface TranslationRepository extends IRepository<Translation> {

	public long deleteForKeyword(Keyword keyword);

	public Collection<Translation> list(String language, String projectId);

//	public Cursor batchProcess(String cursorStr);
}
