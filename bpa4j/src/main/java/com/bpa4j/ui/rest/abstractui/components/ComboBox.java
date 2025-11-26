package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author AI-generated
 */
public class ComboBox extends Component{
	private List<String> items=new ArrayList<>();
	private int selectedIndex=-1;
	private boolean enabled=true;
	private Size preferredSize=new Size(150,25);
	public ComboBox(){
		setSize(preferredSize);
	}
	public ComboBox(List<String> items){
		this();
		setItems(items);
	}
	public List<String> getItems(){
		return items;
	}
	public void setItems(List<String> items){
		this.items=new ArrayList<>();
		if(items!=null) this.items.addAll(items);
		if(this.items.isEmpty()) selectedIndex=-1;
		else if(selectedIndex<0||selectedIndex>=this.items.size()) selectedIndex=0;
		invalidateParent();
	}
	public int getSelectedIndex(){
		return selectedIndex;
	}
	public void setSelectedIndex(int index){
		if(index<-1||index>=items.size()) throw new IllegalArgumentException("Index out of bounds");
		if(this.selectedIndex!=index){
			this.selectedIndex=index;
			invalidateParent();
		}
	}
	public String getSelectedItem(){
		if(selectedIndex<0||selectedIndex>=items.size()) return null;
		return items.get(selectedIndex);
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
	public Size getPreferredSize(){
		return preferredSize;
	}
	public void setPreferredSize(Size size){
		this.preferredSize=size;
		setSize(size);
	}
	public Map<String,Object> getJson(){
		Map<String,Object> json=new HashMap<>();
		json.put("id",getId());
		json.put("type","combobox");
		json.put("x",getX());
		json.put("y",getY());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("items",items);
		json.put("selectedIndex",selectedIndex);
		json.put("selectedItem",getSelectedItem());
		json.put("enabled",enabled);
		return json;
	}
	private void invalidateParent(){
		if(getParent()!=null) getParent().invalidate();
	}
	public void modifyComponent(String id,Map<String,Object> update){
		if(String.valueOf(getId()).equals(id)){
			if(update.containsKey("selectedIndex")){
				Object value=update.get("selectedIndex");
				if(value instanceof Number)setSelectedIndex(((Number)value).intValue());
			}
		}
	}
}
