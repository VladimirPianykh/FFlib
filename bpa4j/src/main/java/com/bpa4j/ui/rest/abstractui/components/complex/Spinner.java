package com.bpa4j.ui.rest.abstractui.components.complex;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;

/**
 * Wraps a panel and two buttons.
 * Delegates all component management methods to the internal panel.
 */
public class Spinner extends Panel{
    private Panel content=new Panel();
    private Button left=new Button("←");
    private Button right=new Button("→");
    public Spinner(){
        BorderLayout layout=new BorderLayout();
        super.setLayout(layout);
        layout.addLayoutComponent(left,BorderLayout.WEST);
        layout.addLayoutComponent(content,BorderLayout.CENTER);
        layout.addLayoutComponent(right,BorderLayout.EAST);
        super.add(left);
        super.add(content);
        super.add(right);
    }
    public void add(Component c){
        content.add(c);
    }
    public void remove(Component c){
        content.remove(c);
    }
    public void removeAll(){
        content.removeAll();
    }
    public void setLayout(LayoutManager layoutManager){
        content.setLayout(layoutManager);
    }
    public LayoutManager getLayout(){
        return content.getLayout();
    }
}
