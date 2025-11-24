package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;

/**
 * @author AI-generated
 */
public class Label extends Component{
	private String text="";
	private Size preferredSize=new Size(100,20);

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
	public Map<String,Object> getJson(){
		Map<String,Object> json=new HashMap<>();
        json.put("id",hashCode());
		json.put("type","label");
		json.put("x",getX());
		json.put("y",getY());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("text",text);
		return json;
	}

	public Size getPreferredSize(){
		return preferredSize;
	}

	public void setPreferredSize(Size size){
		this.preferredSize=size;
		setSize(size);
	}
}
