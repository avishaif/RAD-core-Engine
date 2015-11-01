package engine;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import core.ProcessData;
import core.ThreadData;

/**
 * Reads and verifies the XML file.
 * All data read from XML saved to ConfigurationData class.
 */
public class XMLParser
{
	private SchemaFactory inputScheme;
	private String filePath;
	private final String DEFAULT_XML_PATH = "ApplicationsToConfigure.xml";
	private final String SCHEME_PATH = "XML_Scheme.xsd";
	
	/**
	 * Constructor.
	 * By default the path to the configuration file and the schema is the root folder.
	 * The path to the configuration file can be specified as a parameter. Schema file must be in the root folder. 
	 * @param file
	 * 			Path to external configuration file.
	 */
	public XMLParser(String file)
	{
		inputScheme = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		this.filePath = (file.equals("")) ? DEFAULT_XML_PATH : file;
	}

	/**
	 * Verifies configuration file structure according to the schema file.
	 * @return
	 * 		True if configuration file structure valid, false otherwise.
	 */
	public boolean checkSchema()
	{
		try
		{
			Schema inSchema = inputScheme.newSchema(new File(SCHEME_PATH));
			Validator validator = inSchema.newValidator();
			validator.validate(new StreamSource(new File(filePath)));
		}catch(SAXParseException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * Reads the interval of time required to invoked the engine if periodic invokation set to true. 
	 * @return
	 * 		Integer value of msec.
	 */
	public int getCycleTime()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Element element;
		DocumentBuilder db;
		NodeList nodelist;
		int sleepTime = 0;
		try
		{
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(filePath);
			nodelist = doc.getElementsByTagName("RADScheduling");
			element = (Element) nodelist.item(0);
			if (element.getAttribute("Periodic").equalsIgnoreCase("true"))
				sleepTime = Integer.parseInt(element.getAttribute("PeriodicTimeToWakeUp"));
			else
				sleepTime = 0;
		} 
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return -1;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			return -1;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
		return sleepTime;
	}
	
	/**
	 * Read the data from configuration file and save it ConfigurationData class.
	 * @return
	 * 		True if parsing finished successfully, false otherwise.
	 */
	public boolean getData()
	{
		boolean parseResult = false;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		Element procElement;
		Element threadElement;
		NodeList processInfoList;
		NodeList threadInfoList;
		
		ProcessData process;
		ThreadData thread;
		String pName, tName;
		int priority;
		int[] affinity;
		String[] tempAffinity;
		boolean isJavaThread = false;
		
		try
		{
			db = dbf.newDocumentBuilder();
			doc = db.parse(filePath);
			
			processInfoList = doc.getElementsByTagName("ProcessInfo");
			for(int proc = 0; proc < processInfoList.getLength(); proc++)
			{
				procElement = (Element)processInfoList.item(proc);
				pName = procElement.getAttribute("ProcessName");
				priority = Integer.parseInt(procElement.getAttribute("Priority"));
				tempAffinity = procElement.getAttribute("CpusList").split(",");			
				affinity = new int[tempAffinity.length];
				for(int aff = 0; aff < affinity.length; aff++)
				{
					affinity[aff] = Integer.parseInt(tempAffinity[aff]);
				}
				
				process = new ProcessData(pName, priority, affinity);
				if(procElement.hasChildNodes())
				{
					isJavaThread = (procElement.getAttribute("isNative").equalsIgnoreCase("true")) ? false : true;
					threadInfoList = procElement.getElementsByTagName("ThreadInfo");
					for(int thrd = 0; thrd < threadInfoList.getLength(); thrd++)
					{
						threadElement = (Element)threadInfoList.item(thrd);
						tName = threadElement.getAttribute("ThreadName");
						priority = Integer.parseInt(threadElement.getAttribute("Priority"));
						tempAffinity = threadElement.getAttribute("CpusList").split(",");
						affinity = new int[tempAffinity.length];
						for(int aff = 0; aff < affinity.length; aff++)
						{
							affinity[aff] = Integer.parseInt(tempAffinity[aff]);
						}
						thread = new ThreadData(tName, isJavaThread, priority, affinity);
						process.addThread(thread);
					}
				}
				ConfigurationData.addProcess(process);
			}
			parseResult = true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			parseResult = false;
		}
		return parseResult;
	}
	// end of getData


	/**
	 * Reads the operating system name from the configuration file.
	 * @return
	 * 		Operating system name.
	 */
	public String getOperatingSystem()
	{
		String os = "none";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		NodeList nodeList;
		Element element;
		
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(filePath);
			nodeList = doc.getElementsByTagName("OS");
			element = (Element)nodeList.item(0);
			os = element.getAttribute("OSName");
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return os;
	}
	// end of getOperatingSystem
	
	
}














