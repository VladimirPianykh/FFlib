package com.bpa4j.ui.rest.abstractui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

public class Panel extends Component{
	private boolean valid;
	private LayoutManager layoutManager=new GridLayout();
	private List<Component> components=new ArrayList<>();
	public List<Component> getComponents(){
		return components;
	}
	public void add(Component c){
		if(c==this) throw new IllegalArgumentException("A component cannot be added to itself");
		if(c!=null){
			components.add(c);
			c.setParent(this);
			invalidate();
		}
	}

	public void remove(Component c){
		if(c!=null&&components.remove(c)){
			c.setParent(null);
			invalidate();
		}
	}

	public void removeAll(){
		for(Component c:components){
			c.setParent(null);
		}
		components.clear();
		invalidate();
	}
	public LayoutManager getLayout(){
		return layoutManager;
	}
	public void setLayout(LayoutManager layoutManager){
		this.layoutManager=layoutManager;
		invalidate();
	}
	public void invalidate(){
		if(!isValid()) return;
		this.valid=false;
		for(Component c:getComponents())
			if(c instanceof Panel p) p.invalidate();
	}
	public boolean isValid(){
		return valid;
	}
	public void update(){
		super.update();
		for(Component c:getComponents())
			c.update();
		layoutManager.layout(this);
		valid=true;
	}
	public Map<String,Object> getJson(){
		return Map.of("x",getX(),"y",getY(),"width",getWidth(),"height",getHeight(),"children",components.stream().map(c->c.getJson()).toList());
	}
}
