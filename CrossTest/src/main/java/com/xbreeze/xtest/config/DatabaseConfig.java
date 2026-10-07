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


import jakarta.xml.bind.annotation.XmlAttribute;
import com.xbreeze.xtest.exception.XTestException;

public class DatabaseConfig {
	private String _name;
	private String _catalog;
	private String _schema;
	private String _databaseServerConfigName;
	private DatabaseServerConfig _databaseServerConfig;
	private String _templateName;
	private ObjectTemplateConfig _template;
	private boolean _quoteObjectNames = false;	
	private Integer _commandTimeOut = 0;
	
	public DatabaseConfig(String name, String JDBCUrl, String username, String password, String schema) {
		this._name = name;		
		this._schema = schema;
	}
	
	public DatabaseConfig() {
		super();
	}
	
	@XmlAttribute(name="name")
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	@XmlAttribute(name="catalog")
	public String getCatalog() {
		return this._catalog;
	}
	
	public void setCatalog(String catalog) {
		this._catalog = catalog;
	}
	
	@XmlAttribute(name="schema")
	public String getSchema() {
		return this._schema;
	}
	
	public void setSchema(String schema) {
		this._schema = schema;
	}
	
	@XmlAttribute(name="quoteObjectNames", required=false)
	public boolean getQuoteObjectNames() {
		return this._quoteObjectNames;
	}
	
	public void setQuoteObjectNames(boolean quoteObjectNames) {
		this._quoteObjectNames = quoteObjectNames;
	}
	@XmlAttribute(name="databaseServerConfigName")
	public String getDatabaseServerConfigName() {
		return this._databaseServerConfigName;
	}
	
	public void setDatabaseServerConfigName(String databaseServerConfigName ) {
		this._databaseServerConfigName = databaseServerConfigName;
	}
	
	@XmlAttribute(name="commandTimeOut", required=false)
	public Integer getCommandTimeOut() {
		return this._commandTimeOut;
	}
	
	public void setCommandTimeOut(Integer commandTimeOut) {
		this._commandTimeOut = commandTimeOut;
	}
	
	public void setDatabaseServerConfig(DatabaseServerConfig databaseServerConfig) {
		this._databaseServerConfig = databaseServerConfig;
	}
	
	public DatabaseServerConfig getDatabaseServerConfig() {
		return this._databaseServerConfig;
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

	public void setDatabaseServerAndTemplateConfig(XTestConfig config) throws XTestException{
		this._databaseServerConfig = config.getDatabaseServerConfig(_databaseServerConfigName);
		this._template = config.getObjectTemplateConfig(_templateName);
	}
	
	
	public String getQualifiedTableName(String tableName) {
		// Apply template naming if needed
		if (this.getTemplate() != null) {
			tableName = this._template.applyTemplateToName(tableName);
		}
		
		// If the schema is empty, return only the table name.
		if (this.getSchema() == null || this.getSchema().isEmpty()) {
			if (this.getQuoteObjectNames()) {
				return String.format("\"%s\"", tableName);
			}
			else {				
				return tableName;
			}
		}
		// Otherwise if the schema is set, return the table with it's schema name.
		else if (this.getQuoteObjectNames()) {
			return String.format("\"%s\".\"%s\"", this._schema, tableName);
		}
		else {
			return String.format("%s.%s", this._schema, tableName);
		}
	}
	
	public String getFormattedColumnName(String columnName) {
		if (this.getQuoteObjectNames()) {
			return String.format("\"%s\"", columnName);
		}
		else {
			return columnName;
		}
	}
}
