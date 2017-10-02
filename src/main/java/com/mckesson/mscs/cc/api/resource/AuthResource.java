package com.mckesson.mscs.cc.api.resource;

import static com.mckesson.mscs.cc.common.util.MSCSServerUtils.base64Decode;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mckesson.mscs.cc.api.service.AuthService;
import com.mckesson.mscs.cc.common.model.ResponseMessage;
import com.mckesson.mscs.cc.common.service.LoggingServices;

@Path("/auth")
public class AuthResource {

	private static final String log = AuthResource.class.getSimpleName();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticate(@Context HttpServletRequest request) {

		String logStr = log + " >> " + "authenticate() >> secret:" + request.getHeader("secret") + " >> ";
		LoggingServices.info(logStr + " authenticating user");

		String secret = request.getHeader("secret");
		AuthService service = new AuthService();
		ResponseMessage message = new ResponseMessage();
		boolean result;
		try {
			LoggingServices.info(logStr + " Calling checkLoginCredentials");
			result = service.checkLoginCredentials(base64Decode(secret), message);
			if (result) {
				LoggingServices.info(logStr + " SUCCESS");
				return Response.status(Response.Status.OK).entity(message).build();
			} else {
				LoggingServices.info(logStr + " UNSUCCESSFUL.");
				return Response.status(Response.Status.UNAUTHORIZED).entity(message).build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
		}
	}

}
