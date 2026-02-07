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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.xbreeze.xtest.exception.XTestException;

public class ProcessConfig {

	private String _name;
	private String _container;
	private String _processServerConfigName;
	private String _commandLineConfigName;
	private String _processNamePrefix="";
	private String _templateName = "";
	private ObjectTemplateConfig _template;
	ArrayList<ConfigProperty> _parameters = new ArrayList<>();

	private ProcessServerConfig _processServerConfig;
	private CommandLineConfig _commandLineConfig;
	
	public ProcessConfig(String name, String processServerConfig, String container, String prefix) {
		this._name = name;
		this._container = container;
		this._processServerConfigName = processServerConfig;
		this._processNamePrefix = prefix;
	}
	
	public ProcessConfig() {
		super();
	}
	
	@XmlAttribute(name="container")
	public String getContainer() {
		return this._container;
	}
	
	public void setContainer(String container) {
		this._container = container;
	}
	
	@XmlAttribute(name="processServerConfigName")
	public String getProcessServerConfigName() {
		return this._processServerConfigName;
	}
	
	public void setProcessServerConfigName(String processServerConfigName) {
		this._processServerConfigName = processServerConfigName;
	}

	@XmlAttribute(name="commandLineConfigName")
	public String getCommandLineConfigName() {
		return this._commandLineConfigName;
	}

	public void setCommandLineConfigName(String commandLineConfigName) {
		this._commandLineConfigName = commandLineConfigName;
	}

	public CommandLineConfig getCommandLineConfig() {
		return this._commandLineConfig;
	}

	public void setCommandLineConfig(CommandLineConfig commandLineConfig) {
		this._commandLineConfig = commandLineConfig;
	}

	public String getQualifiedProcessName(String processName) {		
		return this._processNamePrefix.concat(processName);
	}
	
	
	@XmlAttribute(name="name")
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	@XmlAttribute(name="prefix")
	public String getPrefix() {
		return this._processNamePrefix;
	}
	
	public void setPrefix(String prefix) {		
		this._processNamePrefix = prefix;
	}
	
	public void setProcessServerAndTemplateConfig(XTestConfig config) throws XTestException{
		if (_processServerConfigName != null && !_processServerConfigName.isEmpty()) {
			this._processServerConfig = config.getProcessServerConfig(_processServerConfigName);
		}
		if (_commandLineConfigName != null && !_commandLineConfigName.isEmpty()) {
			this._commandLineConfig = config.getCommandLineConfig(_commandLineConfigName);
		}
		this._template = config.getObjectTemplateConfig(_templateName);
	}
	
	public ProcessServerConfig getProcessServerConfig() {
		return this._processServerConfig;
	}

	@XmlAttribute(name="template")
	public String getTemplateName() {
		return _templateName;
	}

	public void setTemplateName(String templateName) {
		this._templateName = templateName;
	}

	public ObjectTemplateConfig getTemplate() {
		return _template;
	}

	public void setTemplate(ObjectTemplateConfig template) {
		this._template = template;
	}
	
	@XmlElement(name="Parameter")
	@XmlElementWrapper(name="Parameters")
	public ArrayList<ConfigProperty> getParameters() {
		return this._parameters;
	}
	
	public void setParameters(ArrayList<ConfigProperty> parameters) {
		this._parameters = parameters;
	}
	
}
