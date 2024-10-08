/*******************************************************************************
 * Copyright (c) 2021 CrossBreeze
 *
 * This file is part of CrossTest.
 *
 * CrossTest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * CrossTest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrossTest.  If not, see <https://www.gnu.org/licenses/>.
 *      
 * Contributors:
 * Willem Otten - CrossBreeze
 * Harmen Wessels - CrossBreeze
 * Jacob Siemaszko - CrossBreeze
 *******************************************************************************/
package com.xbreeze.xtest.process.informaticapowercenter.execution;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.apache.axis.message.SOAPHeaderElement;

import com.xbreeze.xtest.config.ProcessConfig;
import com.xbreeze.xtest.config.ConfigProperty;
import com.xbreeze.xtest.config.ProcessServerConfig;
import com.xbreeze.xtest.exception.XTestProcessException;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.DIServiceInfo;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.DataIntegrationInterfaceProxy;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.ETaskRunMode;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.EWorkflowRunStatus;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.FaultDetails;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.LoginRequest;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.WorkflowDetails;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.WorkflowRequest;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.TaskRequest;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.VoidRequest;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.TaskDetails;
import com.xbreeze.xtest.process.informaticapowercenter.wsdl.Parameter;

public class InformaticaPowerCenterServerInstance {
	private ProcessConfig _processConfig;
	private DataIntegrationInterfaceProxy _proxy;

	public InformaticaPowerCenterServerInstance(ProcessConfig config) throws XTestProcessException {
		_processConfig = config;
		this._proxy = new DataIntegrationInterfaceProxy();		 
	    _proxy.setEndpoint(config.getProcessServerConfig().getServerUrl());
	    initialize();		
	}
	
	public void initialize() throws XTestProcessException
	{
		//Login and store sessionheader
	   LoginRequest lr = new LoginRequest();
	   ProcessServerConfig psc = _processConfig.getProcessServerConfig();
	   
	   lr.setUserName(psc.getProperty("UserName"));
	   lr.setPassword(psc.getProperty("Password"));
	   if (psc.hasProperty("UserNameSpace")) {
		   lr.setUserNameSpace(psc.getProperty("UserNameSpace"));
	   }
	   lr.setRepositoryDomainName(psc.getProperty("Domain"));
	   lr.setRepositoryName(psc.getProperty("Repository"));
	     
	     //Login to server and store session id
	     String sessionID = "unknown";

	     try {
			sessionID = _proxy.login(lr);
			//Set session header as documented on
			//https://docs.informatica.com/data-integration/powercenter/10-2/web-services-provider-guide/writing-client-applications/java-client-application-for-batch-web-services/session-maintenance-in-axis.html
			((org.apache.axis.client.Stub) _proxy.getDataIntegrationInterface()).setHeader(createSessionHeader(sessionID));
			
	     } catch (RemoteException | SOAPException e) {
			// TODO Auto-generated catch block
	    	 e.printStackTrace();
	    	 throw new XTestProcessException(e.getMessage());
	     }
		 
	}
		
	private SOAPHeaderElement createSessionHeader(String sessID) throws SOAPException {
	    String WSSE_NS ="http://www.informatica.com/";
	    String WSSE_PREFIX = "infa";
	    Name hdrname = SOAPFactory.newInstance().createName("Context",WSSE_PREFIX,WSSE_NS);
	    SOAPHeaderElement header = new SOAPHeaderElement(hdrname);
	    SOAPElement token = header.addChildElement("SessionId");
	    token.addTextNode(sessID);
	    return header;
	   }
	
	public void runProcess(ProcessConfig config, String processName) throws XTestProcessException {
		 //Create workflow request
		ProcessServerConfig serverConfig = config.getProcessServerConfig();
		
	     DIServiceInfo dsi = new DIServiceInfo(serverConfig.getProperty("Domain"),  serverConfig.getProperty("IntegrationService"));
	     WorkflowRequest wfr = new WorkflowRequest();
	     wfr.setDIServiceInfo(dsi);
	     wfr.setFolderName(config.getContainer());
	     wfr.setWorkflowName(config.getQualifiedProcessName(processName));
	     wfr.setRequestMode(ETaskRunMode.NORMAL);
	     
	     //Add parameters, set scope to GLOBAL so variables are always applied
	     if (config.getParameters().size() > 0) {
		     Parameter[] wfParameters = new Parameter[config.getParameters().size()];
		     for (int i = 0; i < config.getParameters().size();i++) {
		    	 ConfigProperty cp = config.getParameters().get(i);
		    	 wfParameters[i] = (new Parameter("GLOBAL", cp.getName(), cp.getValue()));
		     }
		     wfr.setParameters(wfParameters);
	     }
	     try {
	    	
			_proxy.startWorkflow(wfr);
			_proxy.waitTillWorkflowComplete(wfr);
			//Get workflow details to obtain the status
			checkWorkflowStatus(wfr);
			
		} catch (RemoteException e) {
			
			System.out.println("Error executing workflow: " + getRemoteExceptionErrorMessage(e));
			WorkflowDetails wfd;
			try {
				wfd = _proxy.getWorkflowDetails(wfr);
				System.out.println("Status: " + wfd.getWorkflowRunStatus().toString());
				System.out.println("error: " + wfd.getRunErrorMessage());
				throw new XTestProcessException(String.format("Workflow status: %s, error: %s",wfd.getWorkflowRunStatus().toString(), wfd.getRunErrorMessage()));
				
			} catch (RemoteException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				throw new XTestProcessException(ex.getMessage());			
			}
		}
	}
	
	public void runTaskFromProcess(ProcessConfig config, String workflowName, String taskPath) throws XTestProcessException {
		 //Create workflow request
		ProcessServerConfig serverConfig = config.getProcessServerConfig();			
		DIServiceInfo dsi = new DIServiceInfo(serverConfig.getProperty("Domain"),  serverConfig.getProperty("IntegrationService"));
	    TaskRequest tr = new TaskRequest();
	    tr.setDIServiceInfo(dsi);
	    tr.setFolderName(config.getContainer());
	    tr.setWorkflowName(workflowName);
	    tr.setRequestMode(ETaskRunMode.NORMAL);
	    tr.setTaskInstancePath(taskPath);

		  //Add parameters, set scope to GLOBAL so variables are always applied
	     if (config.getParameters().size() > 0) {
		     Parameter[] wfParameters = new Parameter[config.getParameters().size()];
		     for (int i = 0; i < config.getParameters().size();i++) {
		    	 ConfigProperty cp = config.getParameters().get(i);
		    	 wfParameters[i] = (new Parameter("GLOBAL", cp.getName(), cp.getValue()));
		     }
		     tr.setParameters(wfParameters);
	     }

	    
	    //Also create workflow request so we can wait for it to complete
	    WorkflowRequest wfr = new WorkflowRequest();
	    wfr.setDIServiceInfo(dsi);
	    wfr.setFolderName(config.getContainer());
	    wfr.setWorkflowName(workflowName);
	    wfr.setRequestMode(ETaskRunMode.NORMAL);
	    
	     try {
	    	 //Start the task, then wait for the entire workflow to complete
	    	 //this to prevent subsequent calls to the same workflow since this leads to errors.
			_proxy.startTask(tr);
			_proxy.waitTillWorkflowComplete(wfr);
			//Get workflow details to obtain the status
			checkWorkflowStatus(wfr);
			
		} catch (RemoteException e) {			
			System.out.println("Error executing task: " + getRemoteExceptionErrorMessage(e));
			TaskDetails td;
			try {
				td = _proxy.getTaskDetails(tr);		
				System.out.println("error: " + td.getRunErrorMessage());;
				throw new XTestProcessException(String.format("Task errorcode: %d, error: %s",td.getRunErrorCode(), td.getRunErrorMessage()));
				
			} catch (RemoteException ex) {				// 
				ex.printStackTrace();
				throw new XTestProcessException(ex.getMessage());			
			}
		}
	}
	
	private void checkWorkflowStatus(WorkflowRequest wfr) throws XTestProcessException {
		//Get workflow details to obtain the status
		try {
			WorkflowDetails wfd = _proxy.getWorkflowDetails(wfr);
			if (
					(wfd.getWorkflowRunStatus() == EWorkflowRunStatus.FAILED) ||
					wfd.getWorkflowRunStatus() == EWorkflowRunStatus.ABORTED
				)
			{
				System.out.println("Workflow did not complete succesfully");
				System.out.println("Status: " + wfd.getWorkflowRunStatus().toString());
				throw new XTestProcessException(String.format("Workflow status: %s consult PowerCenter logging for more information.",wfd.getWorkflowRunStatus().toString()));
			}
		}  catch (RemoteException ex) {	
			ex.printStackTrace();
			throw new XTestProcessException(ex.getMessage());		
		}
	}
	
	private String getRemoteExceptionErrorMessage(RemoteException e) {
		String msg = "Unknown error";
		if (e.getMessage() != null) {
			return e.getMessage();
		}
		if (e instanceof FaultDetails) {
			FaultDetails fd = (FaultDetails) e;
			return String.format("PowerCenter error: %s %s", fd.getErrorCode(), fd.getFaultString()); 
		}
		return msg;
	}
	
	public void closeConnection() throws XTestProcessException {
		try {
			_proxy.logout(new VoidRequest());
		} catch (RemoteException ex) {
			// TODO Auto-generated catch block
			throw new XTestProcessException(ex.getMessage());		
		}
	}
}
