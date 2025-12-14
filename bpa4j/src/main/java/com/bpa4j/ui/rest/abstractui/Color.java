package com.bpa4j.ui.rest.abstractui;

public record Color(int value){
    public Color(int r,int g,int b){
        this((r<<16)|(g<<8)|b);
        if(r<0||r>255||g<0||g>255||b<0||b>255){
            throw new IllegalArgumentException("Color values must be between 0 and 255");
        }
    }
}
