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

import com.xbreeze.xtest.exception.XTestProcessException;

public class ProcessServerConfig {
	String _name;
	String _serverUrl;
	String _executionClass;
	ArrayList<SecurableConfigProperty> _properties;
	
	public ProcessServerConfig(String name, String executionClass, String serverUrl) {
		_name = name;
		_serverUrl = serverUrl;
		_executionClass = executionClass;		
		_properties = new ArrayList<>();
	}
	
	public ProcessServerConfig() {
		super();
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	@XmlAttribute(name="name")
	public String getName() {
		return this._name;
	}
	
	public void setProperty(String propertyKey, String propertyValue, String credentialProvider) {
		_properties.add(new SecurableConfigProperty(propertyKey, propertyValue, credentialProvider));
	}
	
	public String getProperty(String propertyKey) throws XTestProcessException {
		for(ConfigProperty cp:_properties) {
		if (cp.getName().equalsIgnoreCase(propertyKey)) 
			return cp.getValue();
		}
		
		throw new XTestProcessException(String.format("Property %s of process configuration does not exist", propertyKey));
	}
	
	@XmlAttribute(name="serverUrl")
	public String getServerUrl() {
		return _serverUrl;
	}
	
	public void setServerUrl(String serverUrl) {
		this._serverUrl = serverUrl;
	}
	
	@XmlAttribute(name="executionClass")
	public String getExecutionClass() {
		return _executionClass;
	}
	
		
	public void setExecutionClass(String executionClass) {
		this._executionClass = executionClass;		
	}
	
	@XmlElement(name="Property")
	@XmlElementWrapper(name="Properties")
	public ArrayList<SecurableConfigProperty> getProperties() {
		// If there are no properties defined, only create an empty array.
		if (this._properties == null) {
			this._properties = new ArrayList<>();
		}
		return this._properties;
	}
	
	public void setProperties(ArrayList<SecurableConfigProperty> properties) {
		this._properties = properties;
	}
	
	public boolean hasProperty(String propertyKey) {
		for(ConfigProperty cp:_properties) {
			if (cp.getName().equalsIgnoreCase(propertyKey)) 
				return true;
		}
		return false;
	}
}
