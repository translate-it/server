package com.amadeus.ori.translate.exporters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

/**
 * This is a factory to get an "exporter" from a file format type.
 * 
 * An exporter is an object whose purpose is to output the translation to a localization file format, ready to be used.
 * 
 * Supported file formats are:
 * - Java property files (ios)
 * - iOS/Mac OS X property files (java)
 * - Microsoft resource files (resx)
 * - JSON object (json)
 * - Android string resources (android)
 * 
 * @author bbezine
 *
 */
public class ExporterFactory {

	private static final Log LOG = LogFactory.getLog(ExporterFactory.class);

	private static Map<String, Class<? extends Exporter>> exporters = new HashMap<String, Class<? extends Exporter>>();
	private static Map<String, String> exportersDescriptions = new HashMap<String, String>();
	
	static {
		registerAllExporters();
	}
	
	/**
	 * Get the exporter associated to the file format
	 * @param exporterName the name of the exporter. See class info for the list of accepted exporter names.
	 * @return the exporter if available, null otherwise.
	 */
	public static Exporter get(String exporterName) {
		Class<? extends Exporter> exporterClass = exporters.get(exporterName.toLowerCase());
		Exporter output = null;

		if (exporterClass != null) {
			try {
				output = exporterClass.newInstance();
			} catch (InstantiationException e) {
				LOG.error("InstantiationException while instanciating " + exporterClass.getName(), e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				LOG.error("IllegalAccessException while instanciating " + exporterClass.getName(), e);
				e.printStackTrace();
			}
		} else {
			LOG.info("No exporter found for " + exporterClass);
		}
		
		return output;
	}
	
	/**
	 * Get the set of names of the exporters currently available
	 * @return a set containing strings of all the names of the exporters currently available
	 */
	public static Map<String, String> list() {
		return exportersDescriptions;
	}

	/**
	 * Get the current package and find all classes implementing Exporter interface
	 */
	private static void registerAllExporters() {

		Reflections reflections = new Reflections(ExporterFactory.class.getPackage().getName());
		Set<Class<? extends Exporter>> exporterSubTypes = 
	               reflections.getSubTypesOf(Exporter.class);
		
		for(Class<? extends Exporter> exporterClass : exporterSubTypes) {
			ExporterName annotation = exporterClass.getAnnotation(ExporterName.class);
			if (!exporterClass.isInterface() && annotation != null) {
				String name = annotation.name();
				exporters.put(name, exporterClass);
				exportersDescriptions.put(name, annotation.description());
			}
		}
	}
}
