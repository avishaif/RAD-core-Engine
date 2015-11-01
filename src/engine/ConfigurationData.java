package engine;

import java.util.ArrayList;
import java.util.List;

import core.ProcessData;

/**
 * ConfigurationData class hold the data read from XML configuration file.
 * The class has a list of ProcessData type. Each process written in the configuration file added to that list.
 * Process can have a list of threads of ThreadData type.
 */
public class ConfigurationData {

	private static List<ProcessData> data;

	/**
	 * Add process to data list.
	 * @param prcoess
	 * 			Process data of ProcessData type.
	 */
	public static void addProcess(ProcessData prcoess) {
		if (data == null)
			data = new ArrayList<ProcessData>();
		data.add(prcoess);
	}

	/**
	 * Get configuration data.
	 * @return
	 * 		List of ProcessData type.
	 */
	public static List<ProcessData> getData() {
		return data;
	}
}
