package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;

/**
 * A layout manager that arranges components in a rectangular grid with equal-sized cells.
 * <p>
 * All components in the grid are given equal size. Components are placed in cells 
 * from left to right, top to bottom.
 * </p>
 * 
 * <h3>Grid Dimensions</h3>
 * <ul>
 *   <li>If {@code rows > 0}, the number of columns is calculated as needed to fit all components</li>
 *   <li>If {@code rows == 0}, the number of rows is calculated based on {@code cols}</li>
 *   <li>Both {@code rows} and {@code cols} cannot be zero simultaneously</li>
 * </ul>
 * 
 * <h3>Cell Sizing</h3>
 * Cell dimensions are calculated as:
 * <pre>
 * cellWidth = (targetWidth - (cols - 1) * hgap) / cols
 * cellHeight = (targetHeight - (rows - 1) * vgap) / rows
 * </pre>
 * 
 * <h3>Usage Examples</h3>
 * <pre>{@code
 * // Fixed 3x2 grid
 * Panel panel = new Panel(new GridLayout(3, 2, 5, 5));
 * 
 * // Auto-calculate rows, 4 columns
 * Panel panel = new Panel(new GridLayout(0, 4, 10, 10));
 * 
 * // Single column, auto-calculate rows
 * Panel panel = new Panel(new GridLayout(0, 1, 0, 5));
 * }</pre>
 * 
 * @author AI-generated
 * @see FlowLayout
 * @see BorderLayout
 */
public class GridLayout implements LayoutManager{
    private int rows;
    private int cols;
    private int hgap;
    private int vgap;

    /**
     * Creates a grid layout with one row and as many columns as needed.
     * No gaps between components.
     */
    public GridLayout(){
        this(1,0,0,0);
    }

    /**
     * Creates a grid layout with the specified number of rows and columns.
     * No gaps between components.
     * 
     * @param rows the number of rows (0 means auto-calculate based on cols)
     * @param cols the number of columns (0 means auto-calculate based on rows)
     * @throws IllegalArgumentException if both rows and cols are zero
     */
    public GridLayout(int rows,int cols){
        this(rows,cols,0,0);
    }

    /**
     * Creates a grid layout with the specified number of rows, columns, and gaps.
     * 
     * @param rows the number of rows (0 means auto-calculate based on cols)
     * @param cols the number of columns (0 means auto-calculate based on rows)
     * @param hgap the horizontal gap between cells in pixels
     * @param vgap the vertical gap between cells in pixels
     * @throws IllegalArgumentException if both rows and cols are zero
     */
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
            if(ncols==0) ncols=1; // Safety fallback
            nrows=(ncomponents+ncols-1)/ncols;
        }

        if(ncols<=0) ncols=1;
        if(nrows<=0) nrows=1;

        int w=target.getWidth();
        int h=target.getHeight();

        if(w<=0||h<=0){
            // System.err.println("Either width or height is zero: "+w+" "+h+".");
            return;
        }

        int cellW=(w-(ncols-1)*hgap)/ncols;
        int cellH=(h-(nrows-1)*vgap)/nrows;

        if(cellW<0) cellW=0;
        if(cellH<0) cellH=0;

        for(int i=0;i<ncomponents;i++){
            // Calculate grid position for row-major order
            int r=i/ncols;
            int c=i%ncols;

            int x=c*(cellW+hgap);
            int y=r*(cellH+vgap);

            Component comp=target.getComponents().get(i);
            comp.setBounds(x,y,cellW,cellH);
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
