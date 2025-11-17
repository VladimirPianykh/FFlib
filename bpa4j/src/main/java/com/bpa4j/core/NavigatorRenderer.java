package com.bpa4j.core;

import com.bpa4j.navigation.ImplementedInfo;
import com.bpa4j.navigation.Navigator;

/**
 * Navigator renderer.
 * Must be stateless.
 */
public interface NavigatorRenderer{
	public void render(Navigator navigator);
	public void renderInfo(ImplementedInfo info);
	public void init();
}
