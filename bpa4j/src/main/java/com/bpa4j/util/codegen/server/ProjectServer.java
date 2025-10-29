package com.bpa4j.util.codegen.server;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.bpa4j.util.codegen.ProjectGraph;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Embedded REST server for {@link ProjectGraph} using Jersey/Grizzly.
 * Starts on the port provided to the constructor.
 * @author AI-generated
 */
public class ProjectServer{
	private final ProjectGraph graph;
	private final long port;
	private HttpServer server;
	public ProjectServer(ProjectGraph graph){
		this(graph,5616);
	}
	public ProjectServer(ProjectGraph graph,long port){
		this.graph=graph;
		this.port=port;
		start();
	}

	public synchronized void start(){
		if(server!=null)return;
		ResourceConfig rc=new ResourceConfig()
			.register(new AbstractBinder(){
				protected void configure(){bind(graph).to(ProjectGraph.class);} 
			})
			.register(GsonJsonProvider.class)
			.packages("com.bpa4j.util.codegen.rest");
		server=GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:"+port+"/"),rc);
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	public synchronized void stop(){
		if(server==null)return;
		server.shutdownNow();
		server=null;
	}
}
