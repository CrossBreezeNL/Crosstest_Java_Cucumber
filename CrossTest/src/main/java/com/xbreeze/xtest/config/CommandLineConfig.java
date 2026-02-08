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
 *     Jacob Siemaszko - CrossBreeze
 *******************************************************************************/
package com.xbreeze.xtest.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class CommandLineConfig {

	private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

	private String _name;
	private String _tool = IS_WINDOWS ? "cmd.exe" : "bash";
	private String _toolFlags = IS_WINDOWS ? "/c" : "-c";
	private String _workingDirectory;

	public CommandLineConfig() {
		super();
	}

	@XmlAttribute(name="name")
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	@XmlAttribute(name="tool")
	public String getTool() {
		return _tool;
	}

	public void setTool(String tool) {
		this._tool = tool;
	}

	@XmlAttribute(name="toolFlags")
	public String getToolFlags() {
		return _toolFlags;
	}

	public void setToolFlags(String toolFlags) {
		this._toolFlags = toolFlags;
	}

	@XmlAttribute(name="workingDirectory")
	public String getWorkingDirectory() {
		return _workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this._workingDirectory = workingDirectory;
	}
}
