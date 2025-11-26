package com.bpa4j.ui.rest.abstractui.components;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A push button component that can trigger an action when clicked.
 * </p>
 * Has event `onClick`.
 * @author AI-generated
 */
public class Button extends Component{
    private String text;
    private boolean enabled=true;
    private Consumer<Button> onClick;
    private Size preferredSize=new Size(80,30);
    public Button(){
        this("");
    }
    public Button(String text){
        this.text=text!=null?text:"";
        setSize(preferredSize);
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text=text!=null?text:"";
        invalidateParent();
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
    public void setOnClick(Consumer<Button> action){
        this.onClick=action;
    }
    /**
     * Simulates a button click programmatically.
     * Triggers the onClick listener if the button is enabled.
     */
    public void doClick(){
        if(enabled&&onClick!=null){
            onClick.accept(this);
        }
    }
    public Map<String,Object> getJson(){
        Map<String,Object> json=new HashMap<>();
        json.put("id",getId());
        json.put("type","button");
        json.put("x",getX());
        json.put("y",getY());
        json.put("width",getWidth());
        json.put("height",getHeight());
        json.put("text",text);
        json.put("enabled",enabled);
        json.put("onClick",onClick==null?null:getId()+"/onClick");
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
        if(getParent()!=null){
            getParent().invalidate();
        }
    }
    public void callFunction(String id){
        String thisId=getId()+"/onClick";
        if(thisId.equals(id))doClick();
    }
}
