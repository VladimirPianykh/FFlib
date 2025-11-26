package com.bpa4j.ui.rest.abstractui;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Component{
	private static final AtomicLong ID_GENERATOR=new AtomicLong(0);
	private final long id;
	private Component parent;
	private Rect bounds=new Rect(0,0,0,0);
	protected Component(){
		this.id=ID_GENERATOR.incrementAndGet();
	}
	/**
	 * @return Unique identifier for this component
	 */
	public long getId(){
		return id;
	}
	/**
	 * @return The parent component of this component, or null if it has no parent
	 */
	public Component getParent(){
		return parent;
	}
	/**
	 * Sets the parent component. This is called by container components
	 * like Panel when this component is added to them.
	 */
	protected void setParent(Component parent){
		this.parent=parent;
	}
	/**
	 * Marks this component as needing to be laid out.
	 * If this component is inside a container, the container will be invalidated.
	 */
	public void invalidate(){
		if(parent!=null){
			parent.invalidate();
		}
	}
	public Rect getBounds(){
		return bounds;
	}
	public void setBounds(Rect bounds){
		this.bounds=bounds;
		// invalidateState();
	}
	public void setBounds(int x,int y,int w,int h){
		setBounds(new Rect(x,y,w,h));
	}
	public int getX(){
		return bounds.x();
	}
	public void setX(int x){
		setBounds(new Rect(new Point(x,bounds.point().y()),bounds.size()));
	}
	public int getY(){
		return bounds.y();
	}
	public void setY(int y){
		setBounds(new Rect(new Point(bounds.point().x(),y),bounds.size()));
	}
	public int getWidth(){
		return bounds.width();
	}
	public void setWidth(int width){
		setBounds(new Rect(bounds.point(),new Size(width,bounds.size().height())));
	}
	public int getHeight(){
		return bounds.height();
	}
	public void setHeight(int height){
		setBounds(new Rect(bounds.point(),new Size(bounds.size().width(),height)));
	}
	public Size getSize(){
		return bounds.size();
	}
	public void setSize(Size s){
		setWidth(s.width());
		setHeight(s.height());
	}
	public void setSize(int width,int height){
		setSize(new Size(width,height));
	}
	public Point getLocation(){
		return bounds.point();
	}
	public void setLocation(Point p){
		setBounds(new Rect(p,bounds.size()));
	}
	public void setLocation(int x,int y){
		setLocation(new Point(x,y));
	}
	public void update(){}
	public abstract Map<String,Object> getJson();
	/**
	 * Calls component's function with the given id, if there is such function.
	 * Any function ID has form {@code component.getId()+"/"+eventID}.
	 * Does nothing by default.
	 */
    public void callFunction(String id){}
	/**
	 * Modifies component with the given id, if there is such component.
	 * @param id - component id
	 * @param update - mapping of structure keys to new values
	 */
    public void modifyComponent(String id,Map<String,Object>update){}
}
