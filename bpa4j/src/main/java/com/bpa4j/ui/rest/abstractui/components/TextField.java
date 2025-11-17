package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A single-line text input component that allows the user to enter text.
 * Supports text change listeners and basic text manipulation.
 */
public class TextField extends Component{
    private String text="";
    private boolean editable=true;
    private int maxLength=Integer.MAX_VALUE;
    private Consumer<String> onTextChanged;
    private Consumer<TextField> onAction;
    private Size preferredSize=new Size(150,25);

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

    @Override
    public Map<String,Object> getJson(){
        Map<String,Object> json=new HashMap<>();
        json.put("type","textfield");
        json.put("x",getX());
        json.put("y",getY());
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

}
