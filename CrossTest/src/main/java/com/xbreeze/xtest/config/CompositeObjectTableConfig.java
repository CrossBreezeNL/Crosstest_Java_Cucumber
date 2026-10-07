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

public class CompositeObjectTableConfig {
	private String _tableName;
	private String _databaseConfigName;
	
	public CompositeObjectTableConfig() {
		super();
	}
	
	public CompositeObjectTableConfig(String databaseConfigName, String tableName) {
		this._databaseConfigName = databaseConfigName;
		this._tableName = tableName;
	}
	
	@XmlAttribute(name="tableName")
	public String getTableName() {
		return this._tableName;				
	}
	
	public void setTableName(String tableName) {
		this._tableName = tableName;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other != null) {
			if (other instanceof CompositeObjectTableConfig) {
				CompositeObjectTableConfig otherCotc = (CompositeObjectTableConfig) other;
				return this._databaseConfigName.equals(otherCotc.getDatabaseConfigName()) && this._tableName.equals(otherCotc.getTableName());
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this._databaseConfigName.concat(_tableName).hashCode();
	}
	
	@XmlAttribute(name="databaseConfigName")
	public String getDatabaseConfigName() {
		return this._databaseConfigName;
	}
	
	public void setDatabaseConfigName(String databaseConfigName) {
		this._databaseConfigName = databaseConfigName;
	}

}
