package com.xbreeze.xtest.config;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import com.xbreeze.xtest.exception.XTestProcessException;

public class CredentialProviderConfig {
	private String _name;
	private String _executionClass;
	ArrayList<ConfigProperty> _properties;
	
	@XmlAttribute(name="name")
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	
	@XmlAttribute(name="providerClass")
	public String getExecutionClass() {
		return _executionClass;
	}
	public void setExecutionClass(String executionClass) {
		this._executionClass = executionClass;
	}
	
	public CredentialProviderConfig() {
		this._properties = new ArrayList<>();
	}
	
	@XmlElement(name="Property")
	@XmlElementWrapper(name="Properties")
	public ArrayList<ConfigProperty> getProperties() {
		return this._properties;
	}
	
	public void setProperties(ArrayList<ConfigProperty> properties) {
		this._properties = properties;
	}
	
	public void setProperty(String propertyKey, String propertyValue) {
		_properties.add(new ConfigProperty(propertyKey, propertyValue));
	}
	
	public String getProperty(String propertyKey) throws XTestProcessException {
		for(ConfigProperty cp:_properties) {
		if (cp.getName().equalsIgnoreCase(propertyKey)) 
			return cp.getValue();
		}
		
		throw new XTestProcessException(String.format("Property %s of credential provider %s does not exist", propertyKey, _name));
	}
	
		

		
}
