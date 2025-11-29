package com.bpa4j.ui.rest.abstractui;

import java.util.List;
import java.util.Map;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;

public class Window extends Panel{
	private Panel content;
	public Window(Panel content){
		this.content=content;
	}
	public Panel getContent(){
		return content;
	}
	public void setContent(Panel content){
		this.content=content;
	}
	public List<? extends Component> getComponents(){
		return List.of(content);
	}
	public void add(Component c){
		throw new UnsupportedOperationException("Window cannot contain components.");
	}
	public void remove(Component c){
		throw new UnsupportedOperationException("Window cannot contain components.");
	}
	public void removeAll(){
		throw new UnsupportedOperationException("Window cannot contain components.");
	}
	public Map<String,Object>getJson(JsonVisualContext ctx){
		if(!content.isValid())content.update();
		return Map.of("content",content.getJson(ctx));
	}
	public void callFunction(String id){
		content.callFunction(id);
	}	
	public void modifyComponent(String id,Map<String,Object>update){
		content.modifyComponent(id,update);
	}
	public void update(){
		content.update();
	}
}
