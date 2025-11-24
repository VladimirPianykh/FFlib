package com.bpa4j.ui.rest;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.util.codegen.server.GsonJsonProvider;


public class UIServer{
	private final long port;
	private HttpServer server;
	private UIState state;
	public UIServer(UIState state){
		this(state,5617);
	}
	public UIServer(UIState state,long port){
		this.port=port;
		this.state=state;
	}
	public synchronized void start(){
		if(server!=null)return;
		ResourceConfig rc=new ResourceConfig()
			.register(new AbstractBinder(){
				protected void configure(){
					bind(state).to(UIState.class);
				}
			})
			.register(GsonJsonProvider.class)
			.register(UIResource.class);
		server=GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:"+port+"/"),rc);
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	public synchronized void stop(){
		if(server==null)return;
		server.shutdownNow();
		server=null;
	}
}
