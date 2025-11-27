package com.bpa4j.ui.rest.abstractui.components;

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
}
