package com.bpa4j.ui.rest;

import java.util.Map;
import com.bpa4j.ui.rest.abstractui.UIState;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ui")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UIResource{
	@Inject
	UIState state;

	@GET
	@Path("/structure")
	public Response structure(){
		// if(state.isValid()) return Response.noContent().build();
		// else{
		var json=state.getJsonWithValidity();
		System.err.println("Sent structure with validity "+json.get("valid"));
		if(!(boolean)json.get("valid"))System.err.println(json.get("structure"));
		Response r=Response.ok(json).build();
		return r;
		// }
	}

	@POST
	@Path("/call/{function-id}")
	public Response callFunction(@PathParam("function-id") String functionID){
		try{
			state.callFunction(functionID);
			return Response.ok().build();
		}catch(Exception ex){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	@PUT
	@Path("/modify/{component-id}")
	public Response modify(@PathParam("component-id") String componentID,Map<String,Object>update){
		state.modifyComponent(componentID,update);
		return Response.ok().build();
	}
}
