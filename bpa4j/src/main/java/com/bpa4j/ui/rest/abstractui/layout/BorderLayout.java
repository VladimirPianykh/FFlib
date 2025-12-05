package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Point;
import com.bpa4j.ui.rest.abstractui.Size;

/**
 * A border layout lays out a container, arranging and resizing
 * its components to fit in five regions: north, south, east, west, and center.
 * @author AI-generated
 */
public class BorderLayout implements LayoutManager{
    public static final String NORTH="North";
    public static final String SOUTH="South";
    public static final String EAST="East";
    public static final String WEST="West";
    public static final String CENTER="Center";

    private int hgap;
    private int vgap;

    private Component north;
    private Component west;
    private Component east;
    private Component south;
    private Component center;

    public BorderLayout(){
        this(0,0);
    }

    public BorderLayout(int hgap,int vgap){
        this.hgap=hgap;
        this.vgap=vgap;
    }

    @Override
    public void layout(Panel target){
        if(target.getWidth()<=0||target.getHeight()<=0){ return; }

        int top=0;
        int bottom=target.getHeight();
        int left=0;
        int right=target.getWidth();

        // North component
        if(north!=null){
            north.setSize(new Size(right-left,north.getHeight()));
            north.setLocation(new Point(left,top));
            top+=north.getHeight()+vgap;
        }

        // South component
        if(south!=null){
            south.setSize(new Size(right-left,south.getHeight()));
            south.setLocation(new Point(left,bottom-south.getHeight()));
            bottom-=south.getHeight()+vgap;
        }

        // West component
        if(west!=null){
            west.setSize(new Size(west.getWidth(),bottom-top));
            west.setLocation(new Point(left,top));
            left+=west.getWidth()+hgap;
        }

        // East component
        if(east!=null){
            east.setSize(new Size(east.getWidth(),bottom-top));
            east.setLocation(new Point(right-east.getWidth(),top));
            right-=east.getWidth()+hgap;
        }

        // Center component (takes remaining space)
        if(center!=null){
            center.setLocation(new Point(left,top));
            center.setSize(new Size(Math.max(0,right-left),Math.max(0,bottom-top)));
        }
    }

    public void addLayoutComponent(Component comp,String constraints){
        if(constraints==null){
            constraints=CENTER;
        }

        switch(constraints){
            case NORTH:
                north=comp;
                break;
            case SOUTH:
                south=comp;
                break;
            case EAST:
                east=comp;
                break;
            case WEST:
                west=comp;
                break;
            case CENTER:
                center=comp;
                break;
            default:
                throw new IllegalArgumentException("cannot add to layout: unknown constraint: "+constraints);
        }
    }

    public void removeLayoutComponent(Component comp){
        if(comp==north) north=null;
        if(comp==south) south=null;
        if(comp==east) east=null;
        if(comp==west) west=null;
        if(comp==center) center=null;
    }

    public int getHgap(){
        return hgap;
    }

    public void setHgap(int hgap){
        this.hgap=hgap;
    }

    public int getVgap(){
        return vgap;
    }

    public void setVgap(int vgap){
        this.vgap=vgap;
    }
}
