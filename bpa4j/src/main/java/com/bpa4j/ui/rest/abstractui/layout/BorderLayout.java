package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Point;
import com.bpa4j.ui.rest.abstractui.Size;

/**
 * A layout manager that divides a container into five regions: NORTH, SOUTH, EAST, WEST, and CENTER.
 * <p>
 * Each region can contain at most one component. The CENTER region expands to fill all remaining space
 * after the edge regions have been sized.
 * </p>
 * 
 * <h3>Region Sizing Behavior</h3>
 * <ul>
 *   <li><b>NORTH/SOUTH:</b> Take their preferred height and stretch to full width</li>
 *   <li><b>EAST/WEST:</b> Take their preferred width and stretch to remaining height</li>
 *   <li><b>CENTER:</b> Expands to fill all remaining space</li>
 * </ul>
 * 
 * <h3>Layout Order</h3>
 * Components are laid out in this order:
 * <ol>
 *   <li>NORTH (top edge)</li>
 *   <li>SOUTH (bottom edge)</li>
 *   <li>WEST (left edge, between NORTH and SOUTH)</li>
 *   <li>EAST (right edge, between NORTH and SOUTH)</li>
 *   <li>CENTER (remaining space)</li>
 * </ol>
 * 
 * <h3>Usage Example</h3>
 * <pre>{@code
 * Panel panel = new Panel(new BorderLayout(5, 5));
 * BorderLayout layout = (BorderLayout) panel.getLayout();
 * 
 * Panel header = new Panel();
 * header.setSize(800, 60);
 * layout.addLayoutComponent(header, BorderLayout.NORTH);
 * panel.add(header);
 * 
 * Panel content = new Panel();
 * layout.addLayoutComponent(content, BorderLayout.CENTER);
 * panel.add(content);
 * }</pre>
 * 
 * @author AI-generated
 * @see GridLayout
 * @see FlowLayout
 */
public class BorderLayout implements LayoutManager{
	/** The north (top) region */
	public static final String NORTH="North";
	/** The south (bottom) region */
	public static final String SOUTH="South";
	/** The east (right) region */
	public static final String EAST="East";
	/** The west (left) region */
	public static final String WEST="West";
	/** The center region (fills remaining space) */
	public static final String CENTER="Center";

	private int hgap;
	private int vgap;

	private Component north;
	private Component west;
	private Component east;
	private Component south;
	private Component center;

	/**
	 * Creates a border layout with no gaps between components.
	 */
	public BorderLayout(){
		this(0,0);
	}

	/**
	 * Creates a border layout with the specified gaps between components.
	 * 
	 * @param hgap the horizontal gap between regions in pixels
	 * @param vgap the vertical gap between regions in pixels
	 */
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
			Size northPreferredSize=getPreferredSize(north);
			north.setSize(new Size(right-left,northPreferredSize.height()));
			north.setLocation(new Point(left,top));
			top+=northPreferredSize.height()+vgap;
		}

		// South component
		if(south!=null){
			Size southPreferredSize=getPreferredSize(south);
			south.setSize(new Size(right-left,southPreferredSize.height()));
			south.setLocation(new Point(left,bottom-southPreferredSize.height()));
			bottom-=southPreferredSize.height()+vgap;
		}

		// West component
		if(west!=null){
			Size westPreferredSize=getPreferredSize(west);
			west.setSize(new Size(westPreferredSize.width(),bottom-top));
			west.setLocation(new Point(left,top));
			left+=westPreferredSize.width()+hgap;
		}

		// East component
		if(east!=null){
			Size eastPreferredSize=getPreferredSize(east);
			east.setSize(new Size(eastPreferredSize.width(),bottom-top));
			east.setLocation(new Point(right-eastPreferredSize.width(),top));
			right-=eastPreferredSize.width()+hgap;
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

	/**
	 * Returns the preferred size of the component.
	 * By default - a fixed value (150x50).
	 * Can be overridden to use actual preferences.
	 */
	protected Size getPreferredSize(Component comp){
		if(comp.getWidth()>0&&comp.getHeight()>0){ return new Size(comp.getWidth(),comp.getHeight()); }
		return new Size(150,50);
	}
}
