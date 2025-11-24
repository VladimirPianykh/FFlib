package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;

/**
 * @author AI-generated
 */
public class ProgressBar extends Component{
	private int min=0;
	private int max=100;
	private int value=0;
	private boolean indeterminate=false;
	private Size preferredSize=new Size(150,20);

	public ProgressBar(){
		setSize(preferredSize);
	}

	public int getMin(){
		return min;
	}

	public void setMin(int min){
		this.min=min;
		if(max<min) max=min;
		setValue(value);
	}

	public int getMax(){
		return max;
	}

	public void setMax(int max){
		if(max<min) throw new IllegalArgumentException("max < min");
		this.max=max;
		setValue(value);
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		int v=value;
		if(v<min) v=min;
		if(v>max) v=max;
		if(this.value!=v){
			this.value=v;
			if(getParent()!=null) getParent().invalidate();
		}
	}

	public boolean isIndeterminate(){
		return indeterminate;
	}

	public void setIndeterminate(boolean indeterminate){
		if(this.indeterminate!=indeterminate){
			this.indeterminate=indeterminate;
			if(getParent()!=null) getParent().invalidate();
		}
	}

	@Override
	public Map<String,Object> getJson(){
		Map<String,Object> json=new HashMap<>();
        json.put("id",hashCode());
		json.put("type","progressbar");
		json.put("x",getX());
		json.put("y",getY());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("min",min);
		json.put("max",max);
		json.put("value",value);
		json.put("indeterminate",indeterminate);
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
