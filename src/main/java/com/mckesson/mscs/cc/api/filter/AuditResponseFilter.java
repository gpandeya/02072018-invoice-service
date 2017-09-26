package com.mckesson.mscs.cc.api.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;

import com.mckesson.mscs.cc.common.service.LoggingServices;

/**
 * @author erzq6oo
 *
 */

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class AuditResponseFilter implements ContainerResponseFilter {
	
	private static final String log = AuditResponseFilter.class.getSimpleName() + " >> ";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		LoggingServices.info(log + " execution started");
		StringBuffer logBuffer = new StringBuffer();

		logBuffer.append("HTTP Response\n");
		logBuffer.append("Media Type : " + responseContext.getMediaType());
		logBuffer.append("\n");

		logBuffer.append("HTTP response headers:\n");
		Map<String, String> headers = toMap(responseContext.getHeaders());
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("Access-Control-Allow-Headers",
		            "origin, content-type, accept, authorization, X-Requested-With, Content-Type, X-Codingpedia");
		headers.put("Access-Control-Allow-Credentials", "true");
		headers.put("Access-Control-Allow-Methods",
		            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		for (java.util.Map.Entry<String, String> entry : headers.entrySet()) {
			logBuffer.append("	" + entry.getKey() + ":" + entry.getValue());
			logBuffer.append("\n");
		}
		logBuffer.append("\n");

		logBuffer.append("New Cookies: ");
		logBuffer.append("\n");
		for (java.util.Map.Entry<String, NewCookie> entry : responseContext.getCookies().entrySet()) {
			logBuffer.append("	" + entry.getKey() + " :  Cookie : " + entry.getValue().toString());
			logBuffer.append("\n");
		}
		logBuffer.append("\n");

		Object entity = responseContext.getEntity();
		String payload = "";
		if (entity != null) {
			payload = entity.toString();
		}
		logBuffer.append("Response Entity (Payload) : " + payload);

		LoggingServices.info(log + logBuffer.toString());
		
		LoggingServices.info(log + " Completed.");
	}

	private Map<String, String> toMap(MultivaluedMap<String, Object> multiValuedMap) {

		Map<String, String> parameters = new HashMap<String, String>();
		Iterator<String> it = multiValuedMap.keySet().iterator();
		while (it.hasNext()) {
			String theKey = (String) it.next();
			parameters.put(theKey, multiValuedMap.getFirst(theKey).toString());
		}
		return parameters;
	}

}
