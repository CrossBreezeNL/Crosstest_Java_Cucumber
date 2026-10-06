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

public class ObjectTemplateAttributeConfig {

		private String _name;
		private String _value;
		private Integer _seed;
		private Integer _increment;
		private Integer _nextValue;
		
		@XmlAttribute(name="name")
		public String getName() {
			return _name;
		}
		public void setName(String name) {
			this._name = name;
		}
		
		@XmlAttribute(name="value")
		public String getValue() {
			return _value;
		}
		
		public void setValue(String value) {
			this._value = value;
		}
		
		@XmlAttribute(name="seed")
		public Integer getSeed() {
			return _seed;
		}
		
		
		public void setSeed(Integer seed) {
			this._seed = seed;	
			this._nextValue = null;
		}
		
		@XmlAttribute(name="increment")
		public Integer getIncrement() {
			return _increment;
		}
		
		public void setIncrement(Integer increment) {
			this._increment = increment;
		}
				
		public Boolean isAutoIncrement() {
			return this._seed != null && this._increment != null;
		}
		
		//Return the next value in case of an auto increment field spec
		public Integer getNextValue() {
			if (this.isAutoIncrement()) {
				if (this._nextValue == null) {
					this._nextValue = this._seed;
					return this._seed;
				}
				else {
					this._nextValue += this._increment;
					return this._nextValue;
				}			
			}
			else return null;
		}
		
	
}
