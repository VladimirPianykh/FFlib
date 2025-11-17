package com.bpa4j.ui.rest;

import com.bpa4j.ui.rest.abstractui.UIState;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ui")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UIResource{
    @Inject
    UIState state;
    
    //TODO: fill
}
