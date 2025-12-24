package com.bpa4j.core;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

public abstract class Root{
	public static final ClassLoader CL=ClassLoader.getSystemClassLoader(),RCL=Root.class.getClassLoader();
	public static final Dimension SCREEN_SIZE=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize();
	/**
	 * Root directory with '/' at the end.
	 */
	public static final String folder=System.getProperty("user.home")+"/AppData/Local/1C_application/";
	private static final HashMap<String,ImageIcon>imageResources=new HashMap<String,ImageIcon>();
	public static URL getResource(String name){
		URL url=CL.getResource(name);
		if(url==null)url=RCL.getResource(name);
		return url;
	}
	public static InputStream getResourceAsStream(String name){
		InputStream url=CL.getResourceAsStream(name);
		if(url==null)url=RCL.getResourceAsStream(name);
		return url;
	}
	public static ImageIcon loadIcon(String path){
		if(!imageResources.containsKey(path))imageResources.put(path,new ImageIcon(getResource("resources/"+path)));
		return imageResources.get(path);
	}
	public static ImageIcon loadIcon(String path,int w,int h){
		String p=path+":"+w+"x"+h;
		if(!imageResources.containsKey(p))imageResources.put(p,new ImageIcon(new ImageIcon(getResource("resources/"+path)).getImage().getScaledInstance(w,h,0)));
		return imageResources.get(p);
	}
}
