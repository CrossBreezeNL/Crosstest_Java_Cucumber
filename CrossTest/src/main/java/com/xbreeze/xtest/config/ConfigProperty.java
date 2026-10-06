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

public class ConfigProperty {
	protected String _name;
	protected String _value;
	
	public ConfigProperty(String name, String value) {
		this._name = name;
		this._value = value;
	}
	
	public ConfigProperty() {
		super();
	}
		
	@XmlAttribute(name="name")
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	@XmlAttribute(name="value")
	public String getValue() {
		return this._value;
	}
	
	public void setValue(String value) {
		this._value = value;
	}
	
}
