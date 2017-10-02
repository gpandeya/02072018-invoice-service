package com.mckesson.mscs.cc.api.service;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

import com.mckesson.mscs.cc.common.MSCSKeys;
import com.mckesson.mscs.cc.common.connection.MSCSConnectionManager;
import com.mckesson.mscs.cc.common.exception.MSCSSystemException;
import com.mckesson.mscs.cc.common.model.MSCSMessages;
import com.mckesson.mscs.cc.common.model.ResponseMessage;
import com.mckesson.mscs.cc.common.service.LoggingServices;

public class AuthService implements MSCSKeys {

	private static final String log = AuthService.class.getSimpleName();

	public boolean checkLoginCredentials(String secret,	ResponseMessage responseMessage) throws MSCSSystemException{
		MSCSMessages message = new MSCSMessages();
		String logStr = log + " >> checkLoginCredentials() >>";
		String secrets[] = secret.split(":");
		LoggingServices.info(logStr + " token valid ?" + (secret.length() < 2 ? "false" : "true") + " and length is :" + secret.length());
		if (secret.length() < 2) {
			return false;
		}
		boolean result = checkLoginCredentials(secrets[0], secrets[1], message);
		if (responseMessage == null)
			responseMessage = new ResponseMessage();

		responseMessage.addAll(message.getErrorMessages());
		responseMessage.addAll(message.getWarningMessages());
		responseMessage.addAll(message.getInfoMessages());
		LoggingServices.info(logStr + " result >> " + result);

		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean checkLoginCredentials(String userId, String password, MSCSMessages messages) throws MSCSSystemException {
		String logStr = log + "checkLoginCredentials() >> userId: " + userId+ " >> password: " + password;

		ConnectionFactory cf = null;
		Connection connection = null;
		RecordFactory recordFactory = null;
		Interaction interaction = null;
		MappedRecord output = null;
		boolean loginStatus = false;
		try {
			cf = MSCSConnectionManager.getECCJcoConnectionFactoryForSystemUser();
			connection = cf.getConnection();
			recordFactory = cf.getRecordFactory();
			MappedRecord input = recordFactory.createMappedRecord("Z_WEB_USER_LOGIN_CHECK");
			input.put("USER", userId);
			input.put("PASSWORD", password);
			interaction = connection.createInteraction();
			output = (MappedRecord) interaction.execute(null, input);
			MappedRecord retStruct = (MappedRecord) output.get("RETURN");
			String messageNumber = (String) retStruct.get("NUMBER");
			if (messageNumber.equalsIgnoreCase("000")) {
				loginStatus = true;
			} else {
				loginStatus = false;
				messages.addErrorMsg((String) retStruct.get("MESSAGE"));
				LoggingServices.error(logStr + (String) retStruct.get("MESSAGE"));
			}
			LoggingServices.info(logStr + "loginStatus..:  " + loginStatus);
		} catch (Exception e) {
			loginStatus = false;
			LoggingServices.error(logStr + e.getMessage(), e);
			throw new MSCSSystemException(UNABLE_TO_PROCESS_ERROR_MSG);
		} finally {
			try {
				if (interaction != null)
					interaction.close();
			} catch (ResourceException e) {
				LoggingServices.error(logStr + e.getMessage(), e);
			}
			MSCSConnectionManager.releaseECCJcoConnection(connection);
		}
		return loginStatus;
	}
}