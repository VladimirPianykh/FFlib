package com.futurefactory;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.MultipleGradientPaint.CycleMethod;

abstract class Painter{
	public static void lightInterBorder(Graphics2D g2,int w,int h,Color f,int s){
		Color e=new Color(f.getRed(),f.getGreen(),f.getBlue(),0);
		//corners
		g2.setPaint(new RadialGradientPaint(new Rectangle(0,0,w*2/s,h*2/s),new float[]{0,1},new Color[]{e,f},CycleMethod.NO_CYCLE));
		g2.fill(new Rectangle(0,0,w/s,h/s));
		g2.setPaint(new RadialGradientPaint(new Rectangle(w-w*2/s,0,w*2/s,h*2/s),new float[]{0,1},new Color[]{e,f},CycleMethod.NO_CYCLE));
		g2.fill(new Rectangle(w-w/s,0,w/s,h/s));
		g2.setPaint(new RadialGradientPaint(new Rectangle(w-w*2/s,h-h*2/s,w*2/s,h*2/s),new float[]{0,1},new Color[]{e,f},CycleMethod.NO_CYCLE));
		g2.fill(new Rectangle(w-w/s,h-h/s,w/s,h/s));
		g2.setPaint(new RadialGradientPaint(new Rectangle(0,h-h*2/s,w*2/s,h*2/s),new float[]{0,1},new Color[]{e,f},CycleMethod.NO_CYCLE));
		g2.fill(new Rectangle(0,h-h/s,w/s,h/s));
		//borders
		g2.setPaint(new GradientPaint(0,h/2,f,w/s,h/2,e));
		g2.fill(new Rectangle(0,h/s,w,h-h*2/s+1));
		g2.setPaint(new GradientPaint(w/2,0,f,w/2,h/s,e));
		g2.fill(new Rectangle(w/s,0,w-w*2/s+1,h));
		g2.setPaint(new GradientPaint(w,h/2,f,w-w/s,h/2,e));
		g2.fill(new Rectangle(0,h/s,w,h-h*2/s+1));
		g2.setPaint(new GradientPaint(w/2,h,f,w/2,h-h/s,e));
		g2.fill(new Rectangle(w/s,0,w-w*2/s+1,h));
	}
}