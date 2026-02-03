package com.xbreeze.xtest.modules.process;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.xbreeze.xtest.config.ProcessServerConfig;
import com.xbreeze.xtest.config.SecurableConfigProperty;
import com.xbreeze.xtest.config.XTestConfig;
import com.xbreeze.xtest.database.helpers.DataHelper;
import com.xbreeze.xtest.exception.XTestException;
import com.xbreeze.xtest.exception.XTestProcessException;
import com.xbreeze.xtest.modules.security.CredentialProvider_Helper;
import com.xbreeze.xtest.process.execution.ProcessExecutor;

public class Process_Helper {

	private XTestConfig _config;
	private CredentialProvider_Helper _credentialProviderHelper;
	static final Logger logger = Logger.getLogger(DataHelper.class.getName());
	private HashMap<String, ProcessExecutor> _executors;
	
	public Process_Helper(CredentialProvider_Helper credentialProviderHelper, XTestConfig cfg) throws XTestException{
		this._config = cfg.getConfig();
		this._credentialProviderHelper = credentialProviderHelper;
		this._executors = new HashMap<>();
	}
	
	public void RunTemplatedProcess(String processConfig, String processName) throws Throwable{	
		getProcessExecutor(processConfig).runProcess(_config.getProcessConfig(processConfig), processName);		
	}

	public void ExecuteCommand(String command_text) throws Throwable{	
		getProcessExecutor("commandline").runProcess(_config.getProcessConfig("commandline"), command_text);
	}	
	
	public void CloseProcessConnections() throws Throwable {
		logger.info("Closing process connections");
		for (ProcessExecutor pe :_executors.values()) {			
			pe.cleanUp();
		}
	}
	
	private String getResolvedCredential(String credentialProvider, String credential) throws XTestProcessException {
		//If no credential provider was given, return the credential unprocessed
		if (credentialProvider == null || credentialProvider.isEmpty()) {
			return credential;
		} else {
			//Resolve the credential via the credentialprovider
			try {
				return this._credentialProviderHelper.resolveCredential(credentialProvider, credential);
			}catch(XTestException exc) {
				throw new XTestProcessException(String.format("Could not resolve %s using credential provider %s: %s", credential, credentialProvider, exc.getMessage()));
			}
		}
	}
		
	private ProcessExecutor getProcessExecutor(String processConfig) throws XTestProcessException{
		String executorClass = _config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass();
		if (!_executors.containsKey(executorClass)) {
			//Initialize executor and add to collection
			try {
				ProcessServerConfig pc = _config.getProcessConfig(processConfig).getProcessServerConfig();
				//Process all properties via the credentialprovider if specified
				for(SecurableConfigProperty cp:pc.getProperties()) {
					cp.setValue(getResolvedCredential(cp.getCredentialProvider(), cp.getValue()));
				}				
				Class<?> c = this.getClass().getClassLoader().loadClass(_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass());
				ProcessExecutor pe = (ProcessExecutor)c.getDeclaredConstructor().newInstance();
				_executors.put(executorClass, pe);
			} catch (ClassNotFoundException e) {			
				throw new XTestProcessException(String.format("Execution class %s for process config %s was not found",_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass(), processConfig));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new XTestProcessException(String.format("Could not instantiate execution class %s for process config %s: %s",_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass(), processConfig, e.getMessage()));
			}
			
		}
		return _executors.get(executorClass);
	}
	
}
