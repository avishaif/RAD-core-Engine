package engine;

import facade.Facade;


/**
 * The entry point of the engine.
 */
public class Launcher
{
	static XMLParser parser;

	/**
	 * The entry point of the engine.
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Starting Engine...");
		
		
		if (args.length == 0)
		{
			parser = new XMLParser("");
		}
		else
		{
			parser = new XMLParser(args[0]);
		}
		
		if(!parser.checkSchema())
		{
			System.err.println("XML parser failed to verify input file structure. Launcher has stopped.");
			return;
		}
		if(checkSystemParameters())
		{
			if(!Facade.init())
			{
				System.err.println("Launcher failed to load RAD-Core-Lib and has stopped.");
				return;
			}
			if(!parser.getData())
			{
				System.err.println("XML parser failed to read input data from file. Launcher has stopped.");
				return;
			}
			int sleepTime = parser.getCycleTime();
			if (sleepTime > 0) 
			{
				new Thread(new Scheduler(true, sleepTime)).start();;
			} 
			else 
			{
				new Thread(new Scheduler(false, sleepTime)).start();;
			}
		}
		else
		{
			System.err.println("Launcher has found that system parameters in input file are invalid. \nPlease check Operating system value in input file.");
		}
	}

	/**
	 * Checks if parameters written in the XML file matching the system.
	 * @return
	 * 		If all parameters correct returns true, else returns false.
	 */
	private static boolean checkSystemParameters()
	{
		String os = null;
		
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			os = "windows";
		} 
		else if (System.getProperty("os.name").startsWith("Linux"))
		{
			os = "linux";
		}
		else
		{
			os = "other";
		}
		if (!os.equalsIgnoreCase(parser.getOperatingSystem()))
		{
			return false;
		}
		return true;
	}

}






