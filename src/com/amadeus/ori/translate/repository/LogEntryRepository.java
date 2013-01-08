package com.amadeus.ori.translate.repository;

import com.amadeus.ori.translate.domain.LogEntry;

public interface LogEntryRepository extends IRepository<LogEntry>  {

	void addEntry(String string, boolean b);

	void addEntry(String string);

}