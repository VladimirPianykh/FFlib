package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;

/**
 * A grid layout arranges components in a rectangular grid.
 * @author AI-generated
 */
public class GridLayout implements LayoutManager{
    private int rows;
    private int cols;
    private int hgap;
    private int vgap;

    public GridLayout(){
        this(1,0,0,0);
    }

    public GridLayout(int rows,int cols){
        this(rows,cols,0,0);
    }

    public GridLayout(int rows,int cols,int hgap,int vgap){
        if(rows==0&&cols==0){ throw new IllegalArgumentException("rows and cols cannot both be zero"); }
        this.rows=rows;
        this.cols=cols;
        this.hgap=hgap;
        this.vgap=vgap;
    }

    @Override
    public void layout(Panel target){
        int ncomponents=target.getComponents().size();
        if(ncomponents==0){ return; }

        int nrows=rows;
        int ncols=cols;

        if(nrows>0){
            ncols=(ncomponents+nrows-1)/nrows;
        }else{
            nrows=(ncomponents+ncols-1)/ncols;
        }

        int w=target.getWidth();
        int h=target.getHeight();

        if(w<=0||h<=0){
            System.err.println("Either width or height is zero: "+w+" "+h+".");
            return;
        } // Cannot layout with zero dimensions

        w=(w-(ncols-1)*hgap)/ncols;
        h=(h-(nrows-1)*vgap)/nrows;

        for(int c=0,x=0;c<ncols;c++,x+=w+hgap){
            for(int r=0,y=0;r<nrows;r++,y+=h+vgap){
                int i=r*ncols+c;
                if(i<ncomponents){
                    Component comp=target.getComponents().get(i);
                    comp.setBounds(x,y,w,h);
                }
            }
        }
    }

    public int getRows(){
        return rows;
    }

    public void setRows(int rows){
        if(rows==0&&this.cols==0){ throw new IllegalArgumentException("rows and cols cannot both be zero"); }
        this.rows=rows;
    }

    public int getColumns(){
        return cols;
    }

    public void setColumns(int cols){
        if(cols==0&&this.rows==0){ throw new IllegalArgumentException("rows and cols cannot both be zero"); }
        this.cols=cols;
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
