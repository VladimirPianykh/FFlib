package com.bpa4j.ui.rest.abstractui.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import com.bpa4j.ui.rest.abstractui.Color;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Size;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;

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
    private Color background;
    private Color foreground;
    public Button(){
        this("");
    }
    public Button(String text){
        this.text=text!=null?text:"";
        setSize(preferredSize);
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
    public Map<String,Object> getJson(JsonVisualContext ctx){
        Map<String,Object> json=new HashMap<>();
        json.put("id",getId());
        json.put("type","button");
        json.put("x",getX()+ctx.getXDisplacement());
        json.put("y",getY()+ctx.getYDisplacement());
        json.put("width",getWidth());
        json.put("height",getHeight());
        json.put("text",text);
        json.put("enabled",enabled);
        json.put("onClick",onClick==null?null:getId()+"/onClick");
        if(foreground!=null) json.put("foreground",foreground.value());
        if(background!=null) json.put("background",background.value());
        return json;
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
    public Size getPreferredSize(){
        return preferredSize;
    }
    public void setPreferredSize(Size size){
        this.preferredSize=size;
        setSize(size);
    }
    public Color getBackground(){
        return background;
    }
    public void setBackground(Color background){
        this.background=background;
    }
    public Color getForeground(){
        return foreground;
    }
    public void setForeground(Color foreground){
        this.foreground=foreground;
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
