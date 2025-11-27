package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A list component where each item is associated with a date.
 * Supports sorting by date, selection, and date formatting.
 * @author AI-generated
 */
public class DatedList extends Component{
	public static class DatedItem{
		private String text;
		private LocalDate date;

		public DatedItem(String text,LocalDate date){
			this.text=text!=null?text:"";
			this.date=date!=null?date:LocalDate.now();
		}

		public String getText(){
			return text;
		}

		public void setText(String text){
			this.text=text!=null?text:"";
		}

		public LocalDate getDate(){
			return date;
		}

		public void setDate(LocalDate date){
			this.date=date!=null?date:LocalDate.now();
		}

		public Map<String,Object> toJson(DateTimeFormatter formatter){
			Map<String,Object> json=new HashMap<>();
			json.put("text",text);
			json.put("date",date.format(formatter));
			json.put("dateRaw",date.toString());
			return json;
		}
	}

	private List<DatedItem> items=new ArrayList<>();
	private int selectedIndex=-1;
	private boolean enabled=true;
	private boolean sortByDate=false;
	private boolean sortAscending=true;
	private DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private Size preferredSize=new Size(250,300);
	private Consumer<List<DatedItem>> onItemsChanged;
	private Consumer<Integer> onSelectionChanged;

	public DatedList(){
		setSize(preferredSize);
	}

	public DatedList(List<DatedItem> items){
		this();
		setItems(items);
	}

	public List<DatedItem> getItems(){
		return new ArrayList<>(items);
	}

	public void setItems(List<DatedItem> items){
		this.items=new ArrayList<>();
		if(items!=null) this.items.addAll(items);
		if(sortByDate) sortItems();
		if(this.items.isEmpty()) selectedIndex=-1; 
		else if(selectedIndex<0||selectedIndex>=this.items.size()) selectedIndex=0;
		invalidateParent();
		if(onItemsChanged!=null) onItemsChanged.accept(getItems());
	}

	public void addItem(String text,LocalDate date){
		DatedItem item=new DatedItem(text,date);
		items.add(item);
		if(sortByDate) sortItems();
		invalidateParent();
		if(onItemsChanged!=null) onItemsChanged.accept(getItems());
	}

	public void addItem(DatedItem item){
		if(item!=null){
			items.add(item);
			if(sortByDate) sortItems();
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

	public void updateItem(int index,String text,LocalDate date){
		if(index>=0&&index<items.size()){
			DatedItem item=items.get(index);
			item.setText(text);
			item.setDate(date);
			if(sortByDate) sortItems();
			invalidateParent();
			if(onItemsChanged!=null) onItemsChanged.accept(getItems());
		}
	}

	private void sortItems(){
		Comparator<DatedItem> comparator=Comparator.comparing(DatedItem::getDate);
		if(!sortAscending) comparator=comparator.reversed();
		items.sort(comparator);
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

	public DatedItem getSelectedItem(){
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

	public boolean isSortByDate(){
		return sortByDate;
	}

	public void setSortByDate(boolean sortByDate){
		if(this.sortByDate!=sortByDate){
			this.sortByDate=sortByDate;
			if(sortByDate) sortItems();
			invalidateParent();
		}
	}

	public boolean isSortAscending(){
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending){
		if(this.sortAscending!=sortAscending){
			this.sortAscending=sortAscending;
			if(sortByDate) sortItems();
			invalidateParent();
		}
	}

	public DateTimeFormatter getDateFormatter(){
		return dateFormatter;
	}

	public void setDateFormatter(DateTimeFormatter formatter){
		if(formatter!=null){
			this.dateFormatter=formatter;
			invalidateParent();
		}
	}

	public void setDatePattern(String pattern){
		if(pattern!=null){
			this.dateFormatter=DateTimeFormatter.ofPattern(pattern);
			invalidateParent();
		}
	}

	public void setOnItemsChanged(Consumer<List<DatedItem>> listener){
		this.onItemsChanged=listener;
	}

	public void setOnSelectionChanged(Consumer<Integer> listener){
		this.onSelectionChanged=listener;
	}

	@Override
	public Map<String,Object> getJson(JsonVisualContext ctx){
		Map<String,Object> json=new HashMap<>();
		json.put("id",getId());
		json.put("type","datedlist");
		json.put("x",getX()+ctx.getXDisplacement());
        json.put("y",getY()+ctx.getYDisplacement());
		json.put("width",getWidth());
		json.put("height",getHeight());
		List<Map<String,Object>> itemsJson=new ArrayList<>();
		for(DatedItem item:items){
			itemsJson.add(item.toJson(dateFormatter));
		}
		json.put("items",itemsJson);
		json.put("selectedIndex",selectedIndex);
		if(getSelectedItem()!=null){
			json.put("selectedItem",getSelectedItem().toJson(dateFormatter));
		}else{
			json.put("selectedItem",null);
		}
		json.put("enabled",enabled);
		json.put("sortByDate",sortByDate);
		json.put("sortAscending",sortAscending);
		json.put("datePattern",dateFormatter.toString());
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
			if(update.containsKey("sortByDate")){
				Object value=update.get("sortByDate");
				if(value instanceof Boolean) setSortByDate((Boolean)value);
			}
			if(update.containsKey("sortAscending")){
				Object value=update.get("sortAscending");
				if(value instanceof Boolean) setSortAscending((Boolean)value);
			}
			if(update.containsKey("addItem")){
				Object value=update.get("addItem");
				if(value instanceof Map){
					@SuppressWarnings("unchecked")
					Map<String,Object> itemData=(Map<String,Object>)value;
					String text=(String)itemData.get("text");
					String dateStr=(String)itemData.get("date");
					LocalDate date=dateStr!=null?LocalDate.parse(dateStr):LocalDate.now();
					addItem(text,date);
				}
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
					if(updateData.containsKey("index")&&updateData.containsKey("text")&&updateData.containsKey("date")){
						int index=((Number)updateData.get("index")).intValue();
						String text=(String)updateData.get("text");
						String dateStr=(String)updateData.get("date");
						LocalDate date=LocalDate.parse(dateStr);
						updateItem(index,text,date);
					}
				}
			}
		}
	}
}
