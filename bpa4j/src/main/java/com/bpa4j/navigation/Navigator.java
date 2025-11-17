package com.bpa4j.navigation;

import com.bpa4j.core.NavigatorRenderer;
import com.bpa4j.core.ProgramStarter;

/**
 * Displays the list of all program functions designated in the {@code helppath.cfg}.
 * </p>
 * Right now, it is unclear whether this should save some state.
 * @see HelpView
 */
public class Navigator{
	public static NavigatorRenderer renderer;
	public Navigator(){
		renderer.render(this);
	}
	public static void init(){
		renderer=ProgramStarter.getRenderingManager().getNaviatorRenderer();
		renderer.init();
	}
}
