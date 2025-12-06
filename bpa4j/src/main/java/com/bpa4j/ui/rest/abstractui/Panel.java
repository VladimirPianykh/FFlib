package com.bpa4j.ui.rest.abstractui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

public class Panel extends Component{
	private boolean valid;
	private LayoutManager layoutManager;
	private List<Component> components=new ArrayList<>();
	public Panel(LayoutManager layout){
		layoutManager=layout;
	}
	public Panel(LayoutManager layout,Component...components){
		this(layout);
		this.components.addAll(List.of(components));
	}
	public Panel(Component...components){
		this(new GridLayout(),components);
	}
	public List<? extends Component> getComponents(){
		return components;
	}
	public void add(Component c){
		if(c==this) throw new IllegalArgumentException("A component cannot be added to itself");
		Objects.requireNonNull(c,"Component cannot be null.");
		components.add(c);
		c.setParent(this);
		invalidate();
	}
	public void remove(Component c){
		Objects.requireNonNull(c,"Component cannot be null.");
		if(components.remove(c)){
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
	public boolean isValid(){
		return valid;
	}
	public void update(){
		super.update();
		if(layoutManager!=null) layoutManager.layout(this);
		for(Component c:components)
			c.update();
		valid=true;
	}
	public Map<String,Object> getJson(JsonVisualContext ctx){
		JsonVisualContext subCtx=new JsonVisualContext(ctx.getXDisplacement()+getX(),ctx.getYDisplacement()+getY());
		List<Map<String,Object>> ch=components.stream().map(c->c.getJson(subCtx)).toList();
		return Map.of("type","panel","x",getX(),"y",getY(),"width",getWidth(),"height",getHeight(),"children",ch);
	}
	public void callFunction(String id){
		for(Component c:new ArrayList<>(components))
			c.callFunction(id);
	}
	public void modifyComponent(String id,Map<String,Object>update){
		for(Component c:new ArrayList<>(components))
			c.modifyComponent(id,update);
	}
}
