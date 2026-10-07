package com.xbreeze.xtest.config;

import jakarta.xml.bind.annotation.XmlAttribute;

import com.xbreeze.xtest.exception.XTestDatabaseException;

public class DatabaseCustomDataTypeConfig {
	private String _dataType;
	private String _className;
	
	public DatabaseCustomDataTypeConfig() {
		super();
	}

	@XmlAttribute(name="dataType")
	public String getDataType() {
		return _dataType;
	}

	public void setDataType(String dataType) {
		this._dataType = dataType;
	}

	@XmlAttribute(name="className")
	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		this._className = className;
	}	
	
	public Class<?> getClassRef() throws XTestDatabaseException {
		try {
			return this.getClass().getClassLoader().loadClass(_className);
		} catch (ClassNotFoundException e) {
			throw new XTestDatabaseException(String.format("Could not load class %s from custom datatype %s", _className, _dataType));
		}
	}
	
	

}
