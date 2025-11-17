package com.bpa4j.ui.rest.abstractui;

import java.util.Map;

public class Window extends Panel{
	private Panel content;
	public Panel getContent(){
		return content;
	}
	public void setContent(Panel content){
		this.content=content;
	}
	public Map<String,Object>getJson(){
		return Map.of("content",content.getJson());
	}
}
