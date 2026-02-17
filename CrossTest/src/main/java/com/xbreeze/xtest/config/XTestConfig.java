/*******************************************************************************
 * Copyright (c) 2019 CrossBreeze
 *
 *  This file is part of CrossTest.
 *
 *     CrossTest is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CrossTest is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CrossTest.  If not, see <https://www.gnu.org/licenses/>.
 *     
 * Contributors:
 *     Willem Otten - CrossBreeze
 *     Harmen Wessels - CrossBreeze
 *******************************************************************************/
package com.xbreeze.xtest.config;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Filter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.xbreeze.xtest.exception.XTestDatabaseException;
import com.xbreeze.xtest.exception.XTestException;
import com.xbreeze.xtest.exception.XTestProcessException;
import com.xbreeze.xtest.util.FileUtils;
import com.xbreeze.xtest.util.XMLUtils;
import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

@XmlRootElement(name="XTestConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class XTestConfig {

	private XTestConfig _config = null;
	static final Logger logger = Logger.getLogger("");
	private ArrayList<CommandLineConfig> _commandLineConfigs;
	private ArrayList<DatabaseConfig> _databaseConfigs;
	private ArrayList<DatabaseServerConfig> _databaseServerConfigs;
	private ArrayList<ProcessConfig> _processConfigs;
	private ArrayList<ProcessServerConfig> _processServerConfigs;
	private ArrayList<CompositeObjectConfig> _compositeObjects;
	private ArrayList<ObjectTemplateConfig> _objectTemplates;
	private ArrayList<CredentialProviderConfig> _credentialProviders;
	private Boolean _debug = false; 
	private String _emptyStringValue;
	
	public XTestConfig() throws XTestException {
		_commandLineConfigs = new ArrayList<>();
		_databaseConfigs = new ArrayList<>();
		_databaseServerConfigs = new ArrayList<>();
		_processConfigs = new ArrayList<>();
		_processServerConfigs = new ArrayList<>();
		_compositeObjects = new ArrayList<>();
		_objectTemplates = new ArrayList<>();
		_credentialProviders = new ArrayList<>();
		
		
	}
	
	public XTestConfig getConfig() throws XTestException {
		if (_config == null) {
			_config = fromFile("XTestConfig.xml");
			logger.info("Read config from XTestConfig.xml");
		}
		return _config;
	}
	
	private XTestConfig fromFile(String file) throws XTestException{
		XTestConfig cfg = null;
		// Setup the global LogManager.
		LogManager logManager = LogManager.getLogManager();
		
		// Read the logging configuration from the resource file.
		try {
			InputStream logProps =XTestConfig.class.getResourceAsStream("logging.properties");
			logManager.readConfiguration(logProps);
			logProps.close();
		} catch (SecurityException | IOException e) {
			System.err.println(String.format("Error while getting logging configuration", e.getMessage()));			
		}
				
		try {
			//Read file into string and resolve any includes
		
			String resolvedConfig = getResolvedConfig(file);
			JAXBContext jaxbContext = JAXBContext.newInstance(XTestConfig.class);
			Unmarshaller xTestConfigUnmarshaller = jaxbContext.createUnmarshaller();
			Schema configSchema = getSchema();
			xTestConfigUnmarshaller.setSchema(configSchema);
			//Create a SAXParser factory
			 SAXParserFactory spf = SAXParserFactory.newInstance();
			 //Since XInclude processing is handled through custom implementation, disabled in the spf
			 // spf.setXIncludeAware(true);
			 //spf.setNamespaceAware(true);
			 // Create the StreamSource for the schema.
			 spf.setSchema(configSchema);
			 XMLReader xr = spf.newSAXParser().getXMLReader();
			 //Prevent xml:base tags from being added when includes are processed
			 //xr.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
			 SAXSource saxSource = new SAXSource(xr, new InputSource(new StringReader(resolvedConfig)));
			
	 
			// Unmarshal the config.			
			cfg = (XTestConfig) xTestConfigUnmarshaller.unmarshal(saxSource);
			
			//If debug is true, add a console log handler for info messages
			if (cfg.getDebug()) {
			// Add a logger for the console to log message below warning (and error).
				ConsoleHandler outputConsoleHandler = new ConsoleHandler() {
					@Override
					protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
						super.setOutputStream(System.out);
					}
				};
				Level consoleLogLevel = Level.INFO;
				outputConsoleHandler.setLevel(consoleLogLevel);
				// Only log message with a lower level then warning.
				outputConsoleHandler.setFilter(new Filter() {
					@Override
					public boolean isLoggable(LogRecord record) {
						return record.getLevel().intValue() < Level.SEVERE.intValue();
					}
				});
				// Update the log level to the lowest level.
				logger.setLevel(consoleLogLevel);
				logger.addHandler(outputConsoleHandler);
			}
		}	
		catch (JAXBException | SAXException | ParserConfigurationException e) {
			String message = e.getMessage();
			if (message == null && e.getCause() != null) {
				message  =e.getCause().getMessage();
			}
			throw new XTestException(String.format("Error reading CrossTest config file: %s. Current folder is %s", message, Paths.get("").toAbsolutePath()));
		} 
			
			//TODO how to package signed jars?
		
			//Assign all object template configs their parent
			for(ObjectTemplateConfig oConfig:cfg._objectTemplates) {
				oConfig.setParentTemplateConfig(cfg);
			}
			
			//Assign all database configs their corresponding database server config so when used, no reference to global 
			//config is required.
			for(DatabaseConfig dConfig:cfg._databaseConfigs) {
				dConfig.setDatabaseServerAndTemplateConfig(cfg);				
			}
			
			//Assign all process configs their corresponding process server config so when used, no reference to global 
			//config is required.
			for(ProcessConfig pConfig:cfg._processConfigs) {
				pConfig.setProcessServerAndTemplateConfig(cfg);
			}
			
			return cfg;
	}
	
	/**
	 * Reads the xtest config file, resolves all includes and returns the content in a string
	 * @param xtestConfigFileName
	 * @return the filename found at the given path, with all includes resolved
	 * @throws  
	 * @throws XTestException 
	 */
	public static String getResolvedConfig(String xtestConfigFileName) throws XTestException {
		URI configFileUri = Paths.get("", xtestConfigFileName).toAbsolutePath().toUri();
		HashMap<URI, Integer> resolvedIncludes = new HashMap<>();	
		try {
			return getConfigWithResolvedIncludes(FileUtils.getFileContent(configFileUri), configFileUri, 0, resolvedIncludes);
		} 
		catch (IOException exc) {
			throw new XTestException(String.format("Error resolving file %s: %s", xtestConfigFileName, exc.getMessage()));
		}
	}

	/**
	 * Recursively resolve XIncludes in the XTestConfig.xml file 
	 * @param xTestConfig The config that might include XIncludes to resolve
	 * @param basePath The basePath needed for relative inclusions
	 * @param resolvedIncludes A collection of previously resolved includes to detect a cycle of inclusions
	 * @return The inputSource with resolved includes 
	 * @throws XTestException
	 */
	//TODO make this method reusable, now a similar version exists in CrossGenerate
	private static String getConfigWithResolvedIncludes(String xTestConfig, URI configFileUri, int level,  HashMap<URI, Integer> resolvedIncludes) throws XTestException{
		logger.info(String.format("Scanning config file %s for includes", configFileUri.toString()));
		// Check for cycle detection, e.g. an include that is already included previously
		if (resolvedIncludes.containsKey(configFileUri) && resolvedIncludes.get(configFileUri) != level) {
			throw new XTestException(String.format("Config include cycle detected at level %d, file %s is already included previously", level, configFileUri.toString()));
		}
		else if (!resolvedIncludes.containsKey(configFileUri)) {
			resolvedIncludes.put(configFileUri, level);						
		}
		
		// Get basePath of configFile. If the provided URI refers to a file, us its parent path, if it refers to a folder use it as base path
		try {
			URI basePath  = new URI("file:///../");			
			File configFile = new File(configFileUri.getPath());
			if (configFile.isDirectory()) {
				basePath = configFileUri;
			}
			else if (configFile.isFile()) {
				String parentPath = configFile.getParent();
				if (parentPath != null) {			
					basePath = Paths.get(parentPath).toUri();	
				}
			}
			// Resolve basePath to absolute/real path
			try {
				basePath = Paths.get(basePath).toRealPath(LinkOption.NOFOLLOW_LINKS).toUri();
			} catch (IOException e) {
				throw new XTestException(String.format("Error resolving config basePath %s to canonical path: %s", basePath.toString(), e.getMessage()));
			} 
			
			// Open the config file and look for includes		
			// Make this XPath namespace aware so it actually looks for xi:include instead of include in all namespaces
			VTDNav nav = XMLUtils.getVTDNav(xTestConfig, true);
			AutoPilot ap = new AutoPilot(nav);
			// Declare the XInclude namespace.
			ap.declareXPathNameSpace("xi", "http://www.w3.org/2001/XInclude");
			// Search for all xi:include elements.
			ap.selectXPath("//xi:include");
			int includeCount = 0;		
			try {
				XMLModifier vm = new XMLModifier (nav);
				while ((ap.evalXPath()) != -1) {
					// Obtain the filename of include
					AutoPilot ap_href = new AutoPilot(nav);
					ap_href.selectXPath("@href");
					String includeFileLocation = ap_href.evalXPathToString();
					logger.info(String.format("Found include for %s in config file %s", includeFileLocation, configFileUri.toString()));
					// Resolve include to a valid path against the basePath
					logger.info(String.format("base path %s", basePath.toString()));
					Path p = Paths.get(basePath);
					URI includeFileUri = null;
					try {
						includeFileUri = p.resolve(Paths.get(includeFileLocation)).toRealPath(LinkOption.NOFOLLOW_LINKS).toUri();
					} catch (IOException e) {
						throw new XTestException(String.format("Error resolving found include %s for %s to canonical path: %s", includeFileLocation, configFileUri.toString(), e.getMessage()));
					} 
					logger.info(String.format("Resolved include to %s", includeFileUri.toString()));
					
					try {
						// get file contents, recursively processing any includes found
						String includeContents = getConfigWithResolvedIncludes(FileUtils.getFileContent(includeFileUri), includeFileUri, level + 1, resolvedIncludes);

						// Check for xpointer and apply if found
						AutoPilot ap_xpoint = new AutoPilot(nav);
						ap_xpoint.selectXPath("@xpointer");
						String xPoint = ap_xpoint.evalXPathToString();
						if (xPoint != null && xPoint.length() > 0) {
							logger.info(String.format("Found xpointer in include: %s", xPoint));
							includeContents = XMLUtils.getXmlFragment(includeContents, xPoint);
						}
						
						// If the file contains an XML declaration, remove it			
						if (includeContents.startsWith("<?xml")) {
							includeContents = includeContents.replaceFirst("^<\\?xml.*\\?>", "");
						}
						
						// Replace the node with the include contents
						vm.insertAfterElement(includeContents);
						// Then remove the include node
						vm.remove();					
					} catch (IOException e) {
						throw new XTestException(String.format("Could not read contents of included config file %s: %s", includeFileUri.toString(), e.getMessage()));
					}	
					includeCount++;
				}				
				logger.info(String.format("Found %d includes in config file %s", includeCount, configFileUri.toString()));
				//if includes were found, output and parse the modifier and return it, otherwise return the original one
				if (includeCount > 0) {
					String resolvedXGenConfig = XMLUtils.getResultingXml(vm);					
					logger.info(String.format("Config file %s with includes resolved:", configFileUri.toString()));
					logger.info("**** Begin of config file ****");
					logger.info(resolvedXGenConfig);
					logger.info("**** End of config file ****");					
					return resolvedXGenConfig;
				} else {
					return xTestConfig;
				}
			} catch (NavException e) {
				throw new XTestException(String.format("Error scanning %s for includes: %s", configFileUri.toString(),e.getMessage()));
			} 	catch (ModifyException e  ) {
				throw new XTestException(String.format("Error modifying config file %s: %s", configFileUri.toString(), e.getMessage()));
			} 
		} catch (URISyntaxException e) {
			throw new XTestException(String.format("Could not extract base path from config file %s: %s", configFileUri.toString(), e.getMessage()));
		}	catch (XTestException e) {
			throw new XTestException(String.format("Could not read config from %s: %s", configFileUri.toString(),e.getMessage()));		
		} catch (XPathParseException | XPathEvalException e) {
			throw new XTestException(String.format("XPath error scanning for includes in %s: %s", configFileUri.toString(), e.getMessage()));				
		}
	}
	
	
	private static Schema getSchema() throws XTestException {
		InputStream xTestConfigSchemaAsStream = XTestConfig.class.getResourceAsStream("CrossTestConfig.xsd");		
		StreamSource xTestConfigXsdResource = new StreamSource(xTestConfigSchemaAsStream);
		
		// Try to load the schema.
		Schema configSchema;
		try {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			configSchema = sf.newSchema(xTestConfigXsdResource);
		} catch (SAXException e) {
			throw new XTestException(String.format("Couldn't read the schema file (%s): %s", xTestConfigXsdResource.toString(), e.getMessage()));
		}
		return configSchema;
	}
	
	public DatabaseConfig getDatabaseConfig(String configName) throws XTestDatabaseException {
		for (DatabaseConfig dbc:_databaseConfigs) {
			if (dbc.getName().equalsIgnoreCase(configName))
				return dbc;
		}
		//Throw exception if config was not found
		throw new XTestDatabaseException(String.format("Database config [%s] does not exist", configName));		
	}
	
	public DatabaseServerConfig getDatabaseServerConfig(String configName) throws XTestDatabaseException {
		for (DatabaseServerConfig dbc:_databaseServerConfigs) {
			if (dbc.getName().equalsIgnoreCase(configName))
				return dbc;
		}
		//Throw exception if config was not found
		throw new XTestDatabaseException(String.format("Database server config [%s] does not exist", configName));		
	}
	
	public ObjectTemplateConfig getObjectTemplateConfig(String configName) throws XTestException {
		if (configName == null || configName.isEmpty()) {
			return null;
		}
		for (ObjectTemplateConfig otc:_objectTemplates) {
			if (otc.getName().equalsIgnoreCase(configName))
				return otc;
		}
		//Throw exception if config was not found
		throw new XTestDatabaseException(String.format("Object template config [%s] does not exist", configName));		
	}
	
	public ProcessConfig getProcessConfig(String configName) throws XTestProcessException {
		for (ProcessConfig pc:_processConfigs) {
			if (pc.getName().equalsIgnoreCase(configName))
				return pc;
		}
		
		//throw new exception if config was not found
		throw new XTestProcessException(String.format("Process config [%s] does not exist", configName));
	}
	
	public ProcessServerConfig getProcessServerConfig(String configName) throws XTestProcessException {
		for (ProcessServerConfig psc:_processServerConfigs) {
			if (psc.getName().equalsIgnoreCase(configName))
				return psc;
		}

		//throw new exception if config was not found
		throw new XTestProcessException(String.format("Process server config [%s] does not exist", configName));
	}

	public CommandLineConfig getCommandLineConfig(String configName) throws XTestProcessException {
		if (configName == null || configName.isEmpty()) {
			return null;
		}
		for (CommandLineConfig clc:_commandLineConfigs) {
			if (clc.getName().equalsIgnoreCase(configName))
				return clc;
		}
		//throw exception if config was not found
		throw new XTestProcessException(String.format("Commandline config [%s] does not exist", configName));
	}

	public CredentialProviderConfig getCredentialProviderConfig(String configName) throws XTestException {
		if (configName==null || configName.isEmpty()) {
			return null;
		}
		for (CredentialProviderConfig cpc:_credentialProviders) {
			if (cpc.getName().equalsIgnoreCase(configName))
				return cpc;
		}
		//Throw exception if config was not found
		throw new XTestException(String.format("Credential provider config [%s] does not exist", configName));		
	}
	
	@XmlAttribute(name="debug", required = false)
	public Boolean getDebug() {
		return this._debug;
	}
	
	public void setDebug(Boolean debug) {
		this._debug = debug;
	}
	
	@XmlAttribute(name="emptyStringValue", required = false)
	public String getEmptyStringValue() {
		return this._emptyStringValue;
	}
	
	public void setEmptyStringValue(String emptyStringValue) {
		this._emptyStringValue = emptyStringValue;
	}
	
	public boolean hasEmptyStringValue() {
		return this._emptyStringValue != null;
	}
	
	@XmlElement(name="CommandLineConfig")
	@XmlElementWrapper(name="CommandLineConfigs")
	public ArrayList<CommandLineConfig> getCommandLineConfigs(){
		return this._commandLineConfigs;
	}

	public void setCommandLineConfigs(ArrayList<CommandLineConfig> commandLineConfigs) {
		this._commandLineConfigs = commandLineConfigs;
	}

	@XmlElement(name="DatabaseConfig")
	@XmlElementWrapper(name="DatabaseConfigs")
	public ArrayList<DatabaseConfig> getDatabaseConfigs(){
		return this._databaseConfigs;
	}
	public void setDatabaseConfigs(ArrayList<DatabaseConfig> databaseConfigs) {
		this._databaseConfigs = databaseConfigs;
	}
	
	@XmlElement(name="DatabaseServerConfig")
	@XmlElementWrapper(name="DatabaseServerConfigs")
	public ArrayList<DatabaseServerConfig> getDatabaseServerConfigs(){
		return this._databaseServerConfigs;
	}
	
	public void setDatabaseServerConfigs(ArrayList<DatabaseServerConfig> databaseServerConfigs) {
		this._databaseServerConfigs= databaseServerConfigs;
	}	
	@XmlElement(name="ProcessConfig")
	@XmlElementWrapper(name="ProcessConfigs")
	public ArrayList<ProcessConfig> getProcessConfigs(){
		return this._processConfigs;
	}
	
	public void setProcessConfigs(ArrayList<ProcessConfig> processConfigs) {
		this._processConfigs= processConfigs;
	}	
	
	@XmlElement(name="ProcessServerConfig")
	@XmlElementWrapper(name="ProcessServerConfigs")
	public ArrayList<ProcessServerConfig> getProcessServerConfigs(){
		return this._processServerConfigs;
	}
	
	public void setProcessServerConfigs(ArrayList<ProcessServerConfig> processServerConfigs) {
		this._processServerConfigs= processServerConfigs;
	}
	
	@XmlElement(name="CompositeObject")
	@XmlElementWrapper(name="CompositeObjects")
	public ArrayList<CompositeObjectConfig> getCompositeObjects() {
		return this._compositeObjects;
	}
	
	public void setCompositeObjects(ArrayList<CompositeObjectConfig> compositeObjectConfigs) {
		this._compositeObjects = compositeObjectConfigs;
	}
	
	@XmlElement(name="ObjectTemplate")
	@XmlElementWrapper(name="ObjectTemplates")
	public ArrayList<ObjectTemplateConfig> getOjectTemplateConfigs() {
		return this._objectTemplates;
	}
	
	
	
	public void setObjectTemplateConfigs(ArrayList<ObjectTemplateConfig> objectTemplateConfigs) {
		this._objectTemplates = objectTemplateConfigs;
	}
	
	
	@XmlElement(name="CredentialProvider")
	@XmlElementWrapper(name="CredentialProviders")
	public ArrayList<CredentialProviderConfig> getCredentialProviderConfigs() {
		return this._credentialProviders;
	}
	
	public void setCredentialProviderConfigs(ArrayList<CredentialProviderConfig> credentialProviders) {
		this._credentialProviders = credentialProviders;
	}
	
	public CompositeObjectConfig getCompositeObjectConfig(String objectName) throws XTestDatabaseException {
		for (CompositeObjectConfig coc:this._compositeObjects) {
			if (coc.getName().equals(objectName)) {
				return coc;
			}
		}
		throw new XTestDatabaseException(String.format("There is no definition for composite object %s", objectName));
	}
	
	public void addKeyTableForCompositeObject(String compositeObject, String databaseConfig, String keyTable) {
		try {			
			getCompositeObjectConfig(compositeObject).addKeyTable(keyTable, databaseConfig);
		}
		catch (XTestDatabaseException exc) {
			//Composite object was not found, create it
			CompositeObjectConfig coc = new CompositeObjectConfig();
			coc.setName(compositeObject);
			coc.addKeyTable(keyTable, databaseConfig);
			this._compositeObjects.add(coc);
		}
	}
	
	public void addContextTableForCompositeObject(String compositeObject, String databaseConfig, String contextTable) {
		try {			
			getCompositeObjectConfig(compositeObject).addContextTable(contextTable, databaseConfig);
		}
		catch (XTestDatabaseException exc) {
			//Composite object was not found, create it
			CompositeObjectConfig coc = new CompositeObjectConfig();
			coc.setName(compositeObject);
			coc.addContextTable(contextTable, databaseConfig);
			this._compositeObjects.add(coc);
		}
	}
}
