package com.amadeus.ori.translate.repository.impl;

import com.amadeus.ori.translate.domain.LogEntry;
import com.amadeus.ori.translate.repository.LogEntryRepository;

public class LogEntryRepositoryImpl extends AbstractRepositoryImpl<LogEntry> implements LogEntryRepository {

    static {
        factory().register(LogEntry.class); 
    }

	public void addEntry(String message, boolean important) {

		save(new LogEntry(message, important));
		
	}

	public void addEntry(String message) {

		save(new LogEntry(message));
		
	}

}
