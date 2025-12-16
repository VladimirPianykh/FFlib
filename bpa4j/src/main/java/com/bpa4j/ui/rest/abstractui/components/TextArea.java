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
public class TextArea extends Component{
	private String text="";
	private boolean editable=true;
	private int maxLength=Integer.MAX_VALUE;
	private Size preferredSize=new Size(200,80);
	private Color background=new Color(100,0,0);
	private Color foreground=new Color(255,255,255);

	public TextArea(){
		setSize(preferredSize);
	}

	public TextArea(String text){
		this();
		this.text=text!=null?text:"";
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		String newText=text!=null?text:"";
		if(maxLength>=0&&newText.length()>maxLength){
			newText=newText.substring(0,maxLength);
		}
		this.text=newText;
		if(getParent()!=null) getParent().invalidate();
	}

	public boolean isEditable(){
		return editable;
	}

	public void setEditable(boolean editable){
		this.editable=editable;
		if(getParent()!=null) getParent().invalidate();
	}

	public int getMaxLength(){
		return maxLength;
	}

	public void setMaxLength(int maxLength){
		if(maxLength<0) throw new IllegalArgumentException("Max length cannot be negative");
		this.maxLength=maxLength;
		if(text.length()>maxLength){
			setText(text.substring(0,maxLength));
		}
	}

	@Override
	public Map<String,Object> getJson(JsonVisualContext ctx){
		Map<String,Object> json=new HashMap<>();
        json.put("id",getId());
		json.put("type","textarea");
		json.put("x",getX()+ctx.getXDisplacement());
        json.put("y",getY()+ctx.getYDisplacement());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("text",text);
		json.put("editable",editable);
		json.put("maxLength",maxLength);
		if(foreground!=null) json.put("foreground",foreground.value());
		if(background!=null) json.put("background",background.value());
		return json;
	}

	public Size getPreferredSize(){
		return preferredSize;
	}

	public void setPreferredSize(Size size){
		this.preferredSize=size;
		setSize(size);
	}
	public void modifyComponent(String id,Map<String,Object> update){
		if(String.valueOf(getId()).equals(id)){
			if(update.containsKey("text"))text=(String)update.get("text");
		}
	}
	public Color getBackground(){
		return background;
	}
	public void setBackground(Color background){
		this.background=background;
		if(getParent()!=null) getParent().invalidate();
	}
	public Color getForeground(){
		return foreground;
	}
	public void setForeground(Color foreground){
		this.foreground=foreground;
		if(getParent()!=null) getParent().invalidate();
	}
}
