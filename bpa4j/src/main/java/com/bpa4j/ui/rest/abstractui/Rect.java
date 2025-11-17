package com.bpa4j.ui.rest.abstractui;

public record Rect(Point point,Size size){
    public Rect(int x,int y,int width,int height){
        this(new Point(x,y),new Size(width,height));
    }
    public int x(){
        return point.x();
    }
    public int y(){
        return point.y();
    }
    public int width(){
        return size.width();
    }
    public int height(){
        return size.height();
    }
}
