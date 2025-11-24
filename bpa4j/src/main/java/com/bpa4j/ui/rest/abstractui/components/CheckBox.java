package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author AI-generated
 */
public class CheckBox extends Component{
	private String text="";
	private boolean selected=false;
	private boolean enabled=true;
	private Consumer<CheckBox> onChange;
	private Size preferredSize=new Size(120,20);

	public CheckBox(){
		setSize(preferredSize);
	}

	public CheckBox(String text){
		this();
		this.text=text!=null?text:"";
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text=text!=null?text:"";
		invalidateParent();
	}

	public boolean isSelected(){
		return selected;
	}

	public void setSelected(boolean selected){
		if(this.selected!=selected){
			this.selected=selected;
			if(onChange!=null) onChange.accept(this);
			invalidateParent();
		}
	}

	public boolean isEnabled(){
		return enabled;
	}

	public void setEnabled(boolean enabled){
		if(this.enabled!=enabled){
			this.enabled=enabled;
			invalidateParent();
		}
	}

	public void setOnChange(Consumer<CheckBox> onChange){
		this.onChange=onChange;
	}

	public void toggle(){
		if(enabled){
			setSelected(!selected);
		}
	}

	@Override
	public Map<String,Object> getJson(){
		Map<String,Object> json=new HashMap<>();
        json.put("id",hashCode());
		json.put("type","checkbox");
		json.put("x",getX());
		json.put("y",getY());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("text",text);
		json.put("selected",selected);
		json.put("enabled",enabled);
		return json;
	}

	public Size getPreferredSize(){
		return preferredSize;
	}

	public void setPreferredSize(Size size){
		this.preferredSize=size;
		setSize(size);
	}

	private void invalidateParent(){
		if(getParent()!=null) getParent().invalidate();
	}
}
