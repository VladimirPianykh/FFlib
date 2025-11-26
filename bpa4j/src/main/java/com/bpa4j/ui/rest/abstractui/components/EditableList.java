package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * An editable list component that allows users to add, remove, and edit items.
 * Supports selection and provides callbacks for item changes.
 * @author AI-generated
 */
public class EditableList extends Component{
	private List<String> items=new ArrayList<>();
	private int selectedIndex=-1;
	private boolean editable=true;
	private boolean enabled=true;
	private Size preferredSize=new Size(200,300);
	private Consumer<List<String>> onItemsChanged;
	private Consumer<Integer> onSelectionChanged;

	public EditableList(){
		setSize(preferredSize);
	}

	public EditableList(List<String> items){
		this();
		setItems(items);
	}

	public List<String> getItems(){
		return new ArrayList<>(items);
	}

	public void setItems(List<String> items){
		this.items=new ArrayList<>();
		if(items!=null) this.items.addAll(items);
		if(this.items.isEmpty()) selectedIndex=-1; 
		else if(selectedIndex<0||selectedIndex>=this.items.size()) selectedIndex=0;
		invalidateParent();
		if(onItemsChanged!=null) onItemsChanged.accept(getItems());
	}

	public void addItem(String item){
		if(item!=null){
			items.add(item);
			invalidateParent();
			if(onItemsChanged!=null) onItemsChanged.accept(getItems());
		}
	}

	public void removeItem(int index){
		if(index>=0&&index<items.size()){
			items.remove(index);
			if(selectedIndex==index) selectedIndex=-1;
			else if(selectedIndex>index) selectedIndex--;
			invalidateParent();
			if(onItemsChanged!=null) onItemsChanged.accept(getItems());
		}
	}

	public void updateItem(int index,String newValue){
		if(index>=0&&index<items.size()&&newValue!=null){
			items.set(index,newValue);
			invalidateParent();
			if(onItemsChanged!=null) onItemsChanged.accept(getItems());
		}
	}

	public int getSelectedIndex(){
		return selectedIndex;
	}

	public void setSelectedIndex(int index){
		if(index<-1||index>=items.size()) throw new IllegalArgumentException("Index out of bounds");
		if(this.selectedIndex!=index){
			this.selectedIndex=index;
			invalidateParent();
			if(onSelectionChanged!=null) onSelectionChanged.accept(index);
		}
	}

	public String getSelectedItem(){
		if(selectedIndex<0||selectedIndex>=items.size()) return null;
		return items.get(selectedIndex);
	}

	public boolean isEditable(){
		return editable;
	}

	public void setEditable(boolean editable){
		if(this.editable!=editable){
			this.editable=editable;
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

	public void setOnItemsChanged(Consumer<List<String>> listener){
		this.onItemsChanged=listener;
	}

	public void setOnSelectionChanged(Consumer<Integer> listener){
		this.onSelectionChanged=listener;
	}

	@Override
	public Map<String,Object> getJson(){
		Map<String,Object> json=new HashMap<>();
		json.put("id",getId());
		json.put("type","editablelist");
		json.put("x",getX());
		json.put("y",getY());
		json.put("width",getWidth());
		json.put("height",getHeight());
		json.put("items",items);
		json.put("selectedIndex",selectedIndex);
		json.put("selectedItem",getSelectedItem());
		json.put("editable",editable);
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

	@Override
	public void callFunction(String id){
		String itemsChangedId=getId()+"/onItemsChanged";
		if(itemsChangedId.equals(id)&&onItemsChanged!=null) onItemsChanged.accept(getItems());
		String selectionChangedId=getId()+"/onSelectionChanged";
		if(selectionChangedId.equals(id)&&onSelectionChanged!=null) onSelectionChanged.accept(selectedIndex);
	}

	@Override
	public void modifyComponent(String id,Map<String,Object> update){
		if(String.valueOf(getId()).equals(id)){
			if(update.containsKey("selectedIndex")){
				Object value=update.get("selectedIndex");
				if(value instanceof Number) setSelectedIndex(((Number)value).intValue());
			}
			if(update.containsKey("items")){
				Object value=update.get("items");
				if(value instanceof List){
					@SuppressWarnings("unchecked")
					List<String> newItems=(List<String>)value;
					setItems(newItems);
				}
			}
			if(update.containsKey("addItem")){
				Object value=update.get("addItem");
				if(value instanceof String) addItem((String)value);
			}
			if(update.containsKey("removeItem")){
				Object value=update.get("removeItem");
				if(value instanceof Number) removeItem(((Number)value).intValue());
			}
			if(update.containsKey("updateItem")){
				Object value=update.get("updateItem");
				if(value instanceof Map){
					@SuppressWarnings("unchecked")
					Map<String,Object> updateData=(Map<String,Object>)value;
					if(updateData.containsKey("index")&&updateData.containsKey("value")){
						int index=((Number)updateData.get("index")).intValue();
						String newValue=(String)updateData.get("value");
						updateItem(index,newValue);
					}
				}
			}
		}
	}
}
