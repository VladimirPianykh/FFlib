package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Point;
import com.bpa4j.ui.rest.abstractui.Size;

/**
 * A flow layout arranges components in a directional flow, much like lines of text in a paragraph.
 * Supports different flow directions and alignments.
 * @author AI-generated
 */
public class FlowLayout implements LayoutManager{
    /** Horizontal alignment constants */
    public static final int LEFT=0;
    public static final int CENTER=1;
    public static final int RIGHT=2;

    /** Flow direction constants */
    public static final int LTR=0; // Left to right
    public static final int RTL=1; // Right to left
    public static final int TTB=2; // Top to bottom
    public static final int BTT=3; // Bottom to top

    private int align; // Horizontal alignment
    private int direction; // Flow direction
    private int hgap; // Horizontal gap between components
    private int vgap; // Vertical gap between components

    /**
     * Creates a new flow layout with centered alignment, LTR direction, and default gaps.
     */
    public FlowLayout(){
        this(CENTER,LTR,5,5);
    }

    /**
     * Creates a new flow layout with the specified alignment, LTR direction, and default gaps.
     * @param align the alignment value (LEFT, CENTER, or RIGHT)
     */
    public FlowLayout(int align){
        this(align,LTR,5,5);
    }

    /**
     * Creates a new flow layout with the specified alignment, direction, and gaps.
     * @param align the alignment value (LEFT, CENTER, or RIGHT)
     * @param direction the flow direction (LTR, RTL, TTB, or BTT)
     * @param hgap the horizontal gap between components
     * @param vgap the vertical gap between components
     */
    public FlowLayout(int align,int direction,int hgap,int vgap){
        this.align=align;
        this.direction=direction;
        this.hgap=hgap;
        this.vgap=vgap;
    }

    @Override
    public void layout(Panel target){
        int width=target.getWidth();
        int height=target.getHeight();

        // Initialize position based on direction
        int x=(direction==RTL)?width:0;
        int y=(direction==BTT)?height:0;

        int rowSize=0; // Width for horizontal flows, height for vertical flows
        int lineSize=0; // Height for horizontal flows, width for vertical flows

        for(Component comp:target.getComponents()){
            Size prefSize=comp.getSize();

            boolean newLine=false;

            // Check if we need to move to a new line/column
            if(direction==LTR||direction==RTL){
                // Horizontal flow
                int nextX=(direction==LTR)?x+prefSize.width()+hgap:x-prefSize.width()-hgap;
                newLine=(direction==LTR)?(x>0&&nextX>width):(x<width&&nextX<0);
            }else{
                // Vertical flow
                int nextY=(direction==TTB)?y+prefSize.height()+vgap:y-prefSize.height()-vgap;
                newLine=(direction==TTB)?(y>0&&nextY>height):(y<height&&nextY<0);
            }

            if(newLine){
                if(direction==LTR||direction==RTL){
                    // Move to next line in horizontal flow
                    x=(direction==LTR)?0:width;
                    y+=(direction==LTR?1:-1)*(rowSize+vgap);
                    rowSize=0;
                }else{
                    // Move to next column in vertical flow
                    y=(direction==TTB)?0:height;
                    x+=(direction==TTB?1:-1)*(rowSize+hgap);
                    rowSize=0;
                }
            }

            // Set component position
            int compX=(direction==RTL)?x-prefSize.width():x;
            int compY=(direction==BTT)?y-prefSize.height():y;
            comp.setLocation(new Point(compX,compY));
            comp.setSize(prefSize);

            // Update position for next component
            if(direction==LTR){
                x+=prefSize.width()+hgap;
                rowSize=Math.max(rowSize,prefSize.height());
            }else if(direction==RTL){
                x-=prefSize.width()+hgap;
                rowSize=Math.max(rowSize,prefSize.height());
            }else if(direction==TTB){
                y+=prefSize.height()+vgap;
                rowSize=Math.max(rowSize,prefSize.width());
            }else{ // BTT
                y-=prefSize.height()+vgap;
                rowSize=Math.max(rowSize,prefSize.width());
            }
        }

        // Apply alignment for horizontal flows
        if((direction==LTR||direction==RTL)&&align!=LEFT){
            int i=0;
            while(i<target.getComponents().size()){
                Component comp=target.getComponents().get(i);
                int rowY=comp.getLocation().y();
                int rowWidth=0;
                int rowStart=i;

                // Find all components in this row
                while(i<target.getComponents().size()){
                    Component c=target.getComponents().get(i);
                    if(c.getLocation().y()!=rowY) break;
                    rowWidth+=c.getWidth()+hgap;
                    i++;
                }

                if(rowWidth>0){
                    rowWidth-=hgap; // Remove last hgap
                    int xOffset=0;

                    if(align==CENTER){
                        xOffset=(target.getWidth()-rowWidth)/2;
                    }else if(align==RIGHT){
                        xOffset=target.getWidth()-rowWidth;
                    }

                    // Adjust x positions for this row
                    for(int j=rowStart;j<i;j++){
                        Component c=target.getComponents().get(j);
                        c.setLocation(new Point(c.getX()+xOffset,c.getY()));
                    }
                }
            }
        }

        // Apply alignment for vertical flows
        if((direction==TTB||direction==BTT)&&align!=LEFT){
            int i=0;
            while(i<target.getComponents().size()){
                Component comp=target.getComponents().get(i);
                int colX=comp.getLocation().x();
                int colHeight=0;
                int colStart=i;

                // Find all components in this column
                while(i<target.getComponents().size()){
                    Component c=target.getComponents().get(i);
                    if(c.getLocation().x()!=colX) break;
                    colHeight+=c.getHeight()+vgap;
                    i++;
                }

                if(colHeight>0){
                    colHeight-=vgap; // Remove last vgap
                    int yOffset=0;

                    if(align==CENTER){
                        yOffset=(target.getHeight()-colHeight)/2;
                    }else if(align==RIGHT){
                        yOffset=target.getHeight()-colHeight;
                    }

                    // Adjust y positions for this column
                    for(int j=colStart;j<i;j++){
                        Component c=target.getComponents().get(j);
                        c.setLocation(new Point(c.getX(),c.getY()+yOffset));
                    }
                }
            }
        }
    }

    public int getAlignment(){
        return align;
    }

    public void setAlignment(int align){
        this.align=align;
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
