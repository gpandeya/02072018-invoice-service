/**
 * 
 */
package com.mckesson.mscs.cc.api.resource;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author erzq6oo
 *
 */
@Path("test")
public class SampleResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test(){
		return "Test API works!";
	}
	
	@GET
	@Path("items")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getItems(){
		String[] a = {"Oxali", "Aranesp", "C2"};
		return Arrays.asList(a);
	}
}
