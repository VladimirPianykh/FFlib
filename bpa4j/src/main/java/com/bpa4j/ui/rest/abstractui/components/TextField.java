package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Color;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Size;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A single-line text input component that allows the user to enter text.
 * Supports text change listeners and basic text manipulation.
 * </p>
 * Has events `onAction` and `onTextChanged`.
 * @author AI-generated
 */
public class TextField extends Component{
	private String text="";
	private boolean editable=true;
	private int maxLength=Integer.MAX_VALUE;
	private Consumer<String> onTextChanged;
	private Consumer<TextField> onAction;
	private Size preferredSize=new Size(150,25);
	private Color background=new Color(100,0,0);
	private Color foreground=new Color(255,255,255);
	public TextField(){
		setSize(preferredSize);
	}
	public TextField(String text){
		this();
		this.text=text!=null?text:"";
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		String oldText=this.text;
		this.text=text!=null?text:"";
		if(!this.text.equals(oldText)&&onTextChanged!=null){
			onTextChanged.accept(this.text);
		}
		// Invalidate parent if it's a Panel
		if(getParent()!=null&&getParent() instanceof Panel){
			((Panel)getParent()).invalidate();
		}
	}
	public boolean isEditable(){
		return editable;
	}
	public void setEditable(boolean editable){
		this.editable=editable;
		// Invalidate parent if it's a Panel
		if(getParent()!=null&&getParent() instanceof Panel){
			((Panel)getParent()).invalidate();
		}
	}
	public int getMaxLength(){
		return maxLength;
	}
	public void setMaxLength(int maxLength){
		if(maxLength<0){ throw new IllegalArgumentException("Max length cannot be negative"); }
		this.maxLength=maxLength;
		if(text.length()>maxLength){
			setText(text.substring(0,maxLength));
		}
	}
	public void setOnTextChanged(Consumer<String> listener){
		this.onTextChanged=listener;
	}
	public void setOnAction(Consumer<TextField> action){
		this.onAction=action;
	}
	/**
	 * Simulates the user pressing Enter in the text field.
	 * Triggers the action listener if set.
	 */
	public void fireAction(){
		if(onAction!=null){
			onAction.accept(this);
		}
	}
	public Map<String,Object> getJson(JsonVisualContext ctx){
		Map<String,Object> json=new HashMap<>();
        json.put("id",getId());
		json.put("type","textfield");
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
	public void callFunction(String id){
		String thisTextId=getId()+"/onTextChanged";
		if(thisTextId.equals(id)&&onTextChanged!=null)onTextChanged.accept(text);
		String thisActionId=getId()+"/onAction";
		if(thisActionId.equals(id)&&onAction!=null)onAction.accept(this);
	}
	public void modifyComponent(String id,Map<String,Object> update){
		if(String.valueOf(getId()).equals(id)){
			if(update.containsKey("text"))setText((String)update.get("text"));
		}
	}
	public Color getBackground(){
		return background;
	}
	public void setBackground(Color background){
		this.background=background;
		if(getParent()!=null&&getParent() instanceof Panel){
			((Panel)getParent()).invalidate();
		}
	}
	public Color getForeground(){
		return foreground;
	}
	public void setForeground(Color foreground){
		this.foreground=foreground;
		if(getParent()!=null&&getParent() instanceof Panel){
			((Panel)getParent()).invalidate();
		}
	}
}
