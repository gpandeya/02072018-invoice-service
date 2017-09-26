/**
 * 
 */
package com.mckesson.mscs.cc.api.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.mckesson.mscs.cc.common.service.LoggingServices;


/**
 * @author erzq6oo
 *
 */


@PreMatching
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class AuditRequestFilter implements ContainerRequestFilter {

	private static final String log = AuditRequestFilter.class.getSimpleName() + " >> ";
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		LoggingServices.info(log + " execution started");
		StringBuffer logBuffer = new StringBuffer();

		logBuffer.append("HTTP Method:  " + requestContext.getMethod());
		logBuffer.append("\n");

		logBuffer.append("Endpoint URI: " + requestContext.getUriInfo().getRequestUri());
		logBuffer.append("\n");

		logBuffer.append("Cookies: ");
		logBuffer.append("\n");
		for (java.util.Map.Entry<String, Cookie> entry : requestContext.getCookies().entrySet()) {
			logBuffer.append("	" + entry.getKey() + " :  Cookie : " + entry.getValue().toString());
			logBuffer.append("\n");
		}
		logBuffer.append("\n");

		logBuffer.append("HTTP Request headers:\n");
		Map<String, String> headers = toMap(requestContext.getHeaders());
		for (java.util.Map.Entry<String, String> entry : headers.entrySet()) {
			logBuffer.append("	" + entry.getKey() + ":" + entry.getValue());
			logBuffer.append("\n");
		}
		logBuffer.append("\n");

		//ToDo:
		// The below code is failing with error : 
		//Caused by: java.lang.NoSuchMethodError: org.apache.commons.io.IOUtils.toString(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/lang/String;
		//at com.mckesson.mscs.webstore.server.filter.AuditRequestFilter.filter(AuditRequestFilter.java:67)
		/*String payload = IOUtils.toString(requestContext.getEntityStream(), Charset.forName("UTF-8"));
		logBuffer.append("Payload = " + payload);
		requestContext.setEntityStream(IOUtils.toInputStream(payload));
*/
		LoggingServices.info(log + logBuffer.toString());
		
		LoggingServices.info(log + " Completed.");

	}

	private Map<String, String> toMap(MultivaluedMap<String, String> multiValuedMap) {
		Map<String, String> parameters = new HashMap<String, String>();
		Iterator<String> it = multiValuedMap.keySet().iterator();
		while (it.hasNext()) {
			String theKey = (String) it.next();
			parameters.put(theKey, multiValuedMap.getFirst(theKey));
		}
		return parameters;
	 }
}
