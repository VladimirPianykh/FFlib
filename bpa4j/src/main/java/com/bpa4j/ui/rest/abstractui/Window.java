package com.bpa4j.ui.rest.abstractui;

import java.util.List;
import java.util.Map;

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
	public Map<String,Object>getJson(){
		if(!content.isValid())content.update();
		return Map.of("content",content.getJson());
	}
	public void callFunction(String id){
		content.callFunction(id);
	}
	public void update(){
		content.update();
	}
}
