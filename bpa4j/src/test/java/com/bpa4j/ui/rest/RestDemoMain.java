package com.bpa4j.ui.rest;

import java.io.Serializable;
import java.util.List;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.feature.Feature;
import com.bpa4j.ui.rest.abstractui.UIState;

/**
 * Small demo main to run REST/AbstractUI rendering.
 * @author AI-generated
 */
public class RestDemoMain{
	public static enum MyRole implements Role{
		BASIC
	}
	public static class DemoItem implements Serializable{
		private String name;
		public DemoItem(){}
		public DemoItem(String name){
			this.name=name;
		}
		public String toString(){
			return name;
		}
	}
	public static void main(String[]args) throws Exception{
		UIState state=new UIState();
		ProgramStarter.setRenderingManager(new RestRenderingManager(state));
		if(!User.hasUser("rest-demo")){
			User.register("rest-demo","demo",MyRole.BASIC);
		}
		ProgramStarter.register(MyRole.BASIC,new Feature[]{
			ItemList.registerList("Demo list",DemoItem.class)
		},new Permission[]{});
		ItemList.getList("Demo list").setElementSupplier(()->{
			return new java.util.ArrayList<>(List.of(new DemoItem("One"),new DemoItem("Two")));
		});
		User.getUser("rest-demo").login("demo");
		WorkFrame wf=new WorkFrame(User.getActiveUser());
		wf.show();
		UIServer server=new UIServer(state);
		server.start();
		System.out.println("REST demo server started on http://localhost:5617/ui/structure");
		System.out.println("Press ENTER to stop...");
		try{
			System.in.read();
		}finally{
			server.stop();
		}
	}
}
