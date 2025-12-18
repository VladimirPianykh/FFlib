package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Color;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;
import java.util.Map;
import java.util.HashMap;

/**
 * @author AI-generated
 */
public class Label extends Component{
	private String text="";
	private Size preferredSize=new Size(100,20);
	private Color foreground;

	public Label(){
		setSize(preferredSize);
	}

	public Label(String text){
		this();
		this.text=text!=null?text:"";
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text=text!=null?text:"";
		if(getParent()!=null) getParent().invalidate();
	}

	@Override
	public Map<String,Object> getJson(JsonVisualContext ctx){
		Map<String,Object> json=new HashMap<>();
        json.put("id",getId());
		json.put("type","label");
		json.put("x",getX()+ctx.getXDisplacement());
        json.put("y",getY()+ctx.getYDisplacement());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("text",text);
		if(foreground!=null) json.put("foreground",foreground.value());
		return json;
	}

	public Size getPreferredSize(){
		return preferredSize;
	}

	public void setPreferredSize(Size size){
		this.preferredSize=size;
		setSize(size);
	}
	public Color getForeground(){
		return foreground;
	}
	public void setForeground(Color foreground){
		this.foreground=foreground;
		if(getParent()!=null) getParent().invalidate();
	}
}
