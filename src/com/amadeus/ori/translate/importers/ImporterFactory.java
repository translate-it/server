package com.amadeus.ori.translate.importers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

public class ImporterFactory {
	
	private static final Log LOG = LogFactory.getLog(ImporterFactory.class);

	private static Map<String, Class<? extends Importer>> importers = new HashMap<String, Class<? extends Importer>>();
	private static Map<String, String> importersDescriptions = new HashMap<String, String>();
	
	static {
		registerAllImporters();
	}
	
	/**
	 * Get the importer associated to the file format
	 * @param importerName the name of the importer. See class info for the list of accepted importer names.
	 * @return the importer if available, null otherwise.
	 */
	public static Importer get(String importerName) {
		Class<? extends Importer> importerClass = importers.get(importerName.toLowerCase());
		Importer output = null;

		if (importerClass != null) {
			try {
				output = importerClass.newInstance();
			} catch (InstantiationException e) {
				LOG.error("InstantiationException while instanciating " + importerClass.getName(), e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				LOG.error("IllegalAccessException while instanciating " + importerClass.getName(), e);
				e.printStackTrace();
			}
		} else {
			LOG.info("No importer found for " + importerName);
		}
		
		return output;
	}

	/**
	 * Get the set of names of the importers currently available
	 * @return a set containing strings of all the names of the importers currently available
	 */
	public static Map<String, String> list() {
		return importersDescriptions;
	}

	/**
	 * Get the current package and find all classes implementing Importer interface
	 */
	private static void registerAllImporters() {

		Reflections reflections = new Reflections(ImporterFactory.class.getPackage().getName());
		Set<Class<? extends Importer>> importerSubTypes = 
	               reflections.getSubTypesOf(Importer.class);
		
		for(Class<? extends Importer> importerClass : importerSubTypes) {
			ImporterName annotation = importerClass.getAnnotation(ImporterName.class);
			if (!importerClass.isInterface() && annotation != null) {
				String name = annotation.name();
				importers.put(name, importerClass);
				importersDescriptions.put(name, annotation.description());
			}
		}
	}
}
