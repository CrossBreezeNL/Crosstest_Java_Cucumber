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

	private String _name;
	private String _windowsTool = "cmd.exe";
	private String _windowsToolFlag = "/c";
	private String _otherTool = "bash";
	private String _otherToolFlag = "-c";
	private String _windowsWorkingDirectory;
	private String _otherWorkingDirectory;

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

	@XmlAttribute(name="windowsTool")
	public String getWindowsTool() {
		return _windowsTool;
	}

	public void setWindowsTool(String windowsTool) {
		this._windowsTool = windowsTool;
	}

	@XmlAttribute(name="windowsToolFlag")
	public String getWindowsToolFlag() {
		return _windowsToolFlag;
	}

	public void setWindowsToolFlag(String windowsToolFlag) {
		this._windowsToolFlag = windowsToolFlag;
	}

	@XmlAttribute(name="otherTool")
	public String getOtherTool() {
		return _otherTool;
	}

	public void setOtherTool(String otherTool) {
		this._otherTool = otherTool;
	}

	@XmlAttribute(name="otherToolFlag")
	public String getOtherToolFlag() {
		return _otherToolFlag;
	}

	public void setOtherToolFlag(String otherToolFlag) {
		this._otherToolFlag = otherToolFlag;
	}

	@XmlAttribute(name="windowsWorkingDirectory")
	public String getWindowsWorkingDirectory() {
		return _windowsWorkingDirectory;
	}

	public void setWindowsWorkingDirectory(String windowsWorkingDirectory) {
		this._windowsWorkingDirectory = windowsWorkingDirectory;
	}

	@XmlAttribute(name="otherWorkingDirectory")
	public String getOtherWorkingDirectory() {
		return _otherWorkingDirectory;
	}

	public void setOtherWorkingDirectory(String otherWorkingDirectory) {
		this._otherWorkingDirectory = otherWorkingDirectory;
	}

	public String getEffectiveTool() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return _windowsTool;
		} else {
			return _otherTool;
		}
	}

	public String getEffectiveToolFlag() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return _windowsToolFlag;
		} else {
			return _otherToolFlag;
		}
	}

	public String getEffectiveWorkingDirectory() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return _windowsWorkingDirectory;
		} else {
			return _otherWorkingDirectory;
		}
	}
}
