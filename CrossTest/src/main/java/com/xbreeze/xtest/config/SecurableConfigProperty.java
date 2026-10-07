package com.xbreeze.xtest.config;

import jakarta.xml.bind.annotation.XmlAttribute;

public class SecurableConfigProperty extends ConfigProperty {
	private String _credentialProvider;
	
	public SecurableConfigProperty(String name, String value, String credentialProvider) {
		this._name = name;
		this._value = value;
		this._credentialProvider = credentialProvider;
	}
	
	public SecurableConfigProperty() {
		super();
	}
		
	@XmlAttribute(name="credentialProvider")
	public String getCredentialProvider() {
		return this._credentialProvider;
	}
	
	public void setCredentialProvider(String credentialProvider) {
		this._credentialProvider = credentialProvider;
	}
}
