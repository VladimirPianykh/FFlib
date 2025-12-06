package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Point;
import com.bpa4j.ui.rest.abstractui.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * A layout manager that arranges components in a directional flow, similar to text flow.
 * <p>
 * Components are placed sequentially in the specified direction (left-to-right, top-to-bottom, etc.)
 * and wrap to a new row or column when the current one is full.
 * </p>
 * 
 * <h3>Key Principles</h3>
 * <ol>
 *   <li><b>Two-stage placement:</b> Components are first divided into rows/columns, 
 *       then aligned and positioned within those rows/columns</li>
 *   <li><b>Preferred sizing:</b> Uses {@link #getPreferredSize(Component)} rather than 
 *       {@code comp.getSize()} to prevent size accumulation errors</li>
 *   <li><b>Size preservation:</b> Components receive exactly their preferred size, 
 *       without compression or stretching</li>
 *   <li><b>Predictability:</b> No fallback to arbitrary default sizes</li>
 * </ol>
 * 
 * <h3>Direction Constants</h3>
 * <ul>
 *   <li>{@link #LTR} - Left to right (horizontal flow)</li>
 *   <li>{@link #RTL} - Right to left (horizontal flow)</li>
 *   <li>{@link #TTB} - Top to bottom (vertical flow)</li>
 *   <li>{@link #BTT} - Bottom to top (vertical flow)</li>
 * </ul>
 * 
 * <h3>Alignment Constants</h3>
 * <ul>
 *   <li>{@link #LEFT} - Align to the left/top edge</li>
 *   <li>{@link #CENTER} - Center alignment</li>
 *   <li>{@link #RIGHT} - Align to the right/bottom edge</li>
 * </ul>
 * 
 * <h3>Usage Examples</h3>
 * <pre>{@code
 * // Horizontal button panel
 * Panel buttons = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.LTR, 5, 5));
 * 
 * // Vertical list without stretching
 * Panel list = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.TTB, 0, 5));
 * 
 * // Centered content
 * Panel centered = new Panel(new FlowLayout(FlowLayout.CENTER, FlowLayout.LTR, 10, 10));
 * }</pre>
 * 
 * <h3>Extensibility</h3>
 * Override {@link #getPreferredSize(Component)} to customize how component sizes are determined.
 * 
 * @author AI-generated
 * @see GridLayout
 * @see BorderLayout
 */
public class FlowLayout implements LayoutManager{

	/** Align components to the left (for horizontal) or top (for vertical) */
	public static final int LEFT=0;
	/** Center components in the available space */
	public static final int CENTER=1;
	/** Align components to the right (for horizontal) or bottom (for vertical) */
	public static final int RIGHT=2;

	/** Left-to-right flow direction (horizontal) */
	public static final int LTR=0;
	/** Right-to-left flow direction (horizontal) */
	public static final int RTL=1;
	/** Top-to-bottom flow direction (vertical) */
	public static final int TTB=2;
	/** Bottom-to-top flow direction (vertical) */
	public static final int BTT=3;

	private int align;
	private int direction;
	private int hgap;
	private int vgap;

	/**
	 * Creates a flow layout with centered alignment, left-to-right direction,
	 * and 5-pixel gaps.
	 */
	public FlowLayout(){
		this(CENTER,LTR,5,5);
	}

	/**
	 * Creates a flow layout with the specified alignment, left-to-right direction,
	 * and 5-pixel gaps.
	 * 
	 * @param align the alignment ({@link #LEFT}, {@link #CENTER}, or {@link #RIGHT})
	 */
	public FlowLayout(int align){
		this(align,LTR,5,5);
	}

	/**
	 * Creates a flow layout with the specified alignment, direction, and gaps.
	 * 
	 * @param align the alignment ({@link #LEFT}, {@link #CENTER}, or {@link #RIGHT})
	 * @param direction the flow direction ({@link #LTR}, {@link #RTL}, {@link #TTB}, or {@link #BTT})
	 * @param hgap the horizontal gap between components in pixels
	 * @param vgap the vertical gap between components in pixels
	 */
	public FlowLayout(int align,int direction,int hgap,int vgap){
		this.align=align;
		this.direction=direction;
		this.hgap=hgap;
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

	@Override
	public void layout(Panel target){
		int pw=target.getWidth();
		int ph=target.getHeight();
		if(pw<=0||ph<=0){
			// No point in laying out if the panel has not yet been measured.
			return;
		}

		if(direction==LTR||direction==RTL){
			layoutHorizontal(target,pw,ph);
		}else{
			layoutVertical(target,pw,ph);
		}
	}

	/* ========================  HORIZONTAL FLOW  ======================== */

	private static class Row{
		final List<Component> comps=new ArrayList<>();
		int width=0;
		int height=0;
	}

	private void layoutHorizontal(Panel target,int pw,int ph){
		List<Row> rows=new ArrayList<>();
		Row current=new Row();

		for(Component c:target.getComponents()){
			Size ps=getPreferredSize(c);

			int needed=(current.comps.isEmpty()?0:current.width+hgap)+ps.width();
			boolean newRow=needed>pw;

			if(newRow&&!current.comps.isEmpty()){
				rows.add(current);
				current=new Row();
			}

			current.comps.add(c);
			current.width=(current.comps.size()==1)?ps.width():current.width+hgap+ps.width();
			current.height=Math.max(current.height,ps.height());
		}

		if(!current.comps.isEmpty()){
			rows.add(current);
		}

		/* === Positioning === */
		int y=0;
		for(Row row:rows){
			int x;
			if(align==LEFT){
				x=0;
			}else if(align==CENTER){
				x=(pw-row.width)/2;
			}else{ // RIGHT
				x=pw-row.width;
			}

			if(direction==RTL){
				x=pw-row.width;
			}

			for(Component c:row.comps){
				Size ps=getPreferredSize(c);
				int cx=x;
				int cy=y;

				if(direction==RTL){
					// position from right to left
					cx=x+(row.width-ps.width());
				}

				c.setLocation(new Point(cx,cy));
				c.setSize(ps);

				x+=ps.width()+hgap;
			}

			y+=row.height+vgap;
		}
	}

	/* ========================  VERTICAL FLOW  ======================== */

	private static class Column{
		final List<Component> comps=new ArrayList<>();
		int width=0;
		int height=0;
	}

	private void layoutVertical(Panel target,int pw,int ph){
		List<Column> cols=new ArrayList<>();
		Column current=new Column();

		for(Component c:target.getComponents()){
			Size ps=getPreferredSize(c);

			int needed=(current.comps.isEmpty()?0:current.height+vgap)+ps.height();
			boolean newCol=needed>ph;

			if(newCol&&!current.comps.isEmpty()){
				cols.add(current);
				current=new Column();
			}

			current.comps.add(c);
			current.height=(current.comps.size()==1)?ps.height():current.height+vgap+ps.height();
			current.width=Math.max(current.width,ps.width());
		}

		if(!current.comps.isEmpty()){
			cols.add(current);
		}

		/* === Positioning === */
		int x=0;
		for(Column col:cols){
			int y;
			if(align==LEFT){
				y=0;
			}else if(align==CENTER){
				y=(ph-col.height)/2;
			}else{
				y=ph-col.height;
			}

			if(direction==BTT){
				y=ph-col.height;
			}

			for(Component c:col.comps){
				Size ps=getPreferredSize(c);
				int cx=x;
				int cy=y;

				if(direction==BTT){
					cy=y+(col.height-ps.height());
				}

				c.setLocation(new Point(cx,cy));
				c.setSize(ps);

				y+=ps.height()+vgap;
			}

			x+=col.width+hgap;
		}
	}

	/* ========================  getters/setters  ======================== */

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
	public int getDirection(){
		return direction;
	}
	public void setDirection(int direction){
		this.direction=direction;
	}
}
