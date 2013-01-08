package com.amadeus.ori.translate.repository;

import java.util.Collection;

import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.domain.Keyword;
import com.amadeus.ori.translate.domain.Translation;



public interface CacheManager {



	public void clearAll();

	public Collection<Keyword> getOrPutKeywordList(KeywordRepository keywordRepository);
	public Keyword putKeyword(Keyword keyword, KeywordRepository keywordRepository);
	
	public Translation getOrPutTranslation(Keyword keyword, String language, TranslationRepository translationRepository);
	public Translation putTranslation(Translation t, TranslationRepository translationRepository);

	public Collection<Project> getOrPutProjectList(ProjectRepository projectRepository);	
	public Project putProject(Project b, ProjectRepository projectRepository);



}