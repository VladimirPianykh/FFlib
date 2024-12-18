package com.futurefactory.defaults.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.JPanel;

import com.futurefactory.HButton;
import com.futurefactory.Wrapper;
import com.futurefactory.core.ProgramStarter;
import com.futurefactory.core.Data.Editable;
import com.futurefactory.core.User.Feature;

public class DisposableDocument<T extends Editable>implements Feature{
	private static HashMap<String,DisposableDocument<?>>documents=new HashMap<>();
	private String name;
	private Class<T>type;
	private Consumer<T>processor;
	private DisposableDocument(String name,Class<T>type){this.name=name;this.type=type;}
	@SuppressWarnings("unchecked")
	public static<T extends Editable>DisposableDocument<T>registerDocument(String name,Class<T>type){
		if(!documents.containsKey(name))documents.put(name,new DisposableDocument<>(name,type));
		if(documents.get(name).type.equals(type))return(DisposableDocument<T>)documents.get(name);
		else throw new IllegalStateException("\""+name+"\" has already been registered with the type"+documents.get(name).type+".");
	}
	@SuppressWarnings("unchecked")
	public static<T extends Editable>DisposableDocument<T>getDocument(String name){
		if(documents.containsKey(name))return(DisposableDocument<T>)documents.get(name);
		else throw new IllegalArgumentException("Document \""+name+"\" does not exist.");
	}
	/**
	 * Sets the processor ({@code Consumer}) to be called when "confirm" button pressed.
	 * @param processor - processor to use
	 */
	public DisposableDocument<T>setProcessor(Consumer<T>processor){this.processor=processor;return this;}
	public void fillTab(JPanel content,JPanel tab,Font font){
		Wrapper<T>doc=new Wrapper<T>(null);
		HButton b=new HButton(){
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				if(doc.var!=null){
					int c=100-scale*6;
					if(getModel().isPressed())c-=25;
					g2.setColor(new Color(c,c,c));
				}
				g2.setColor(Color.LIGHT_GRAY);
				g2.setStroke(doc.var==null?new BasicStroke(getHeight()/60,1,1,2,new float[]{getHeight()/50,getHeight()/50},scale*getHeight()/300):new BasicStroke(getHeight()/40,1,1));
				g2.drawRoundRect(getWidth()/4,getHeight()/6,getWidth()/2,getHeight()*2/3,getHeight()/10,getHeight()/10);
				for(int i=0;i<4;++i)g2.drawLine(getWidth()/3,getHeight()/5+i*getHeight()/6,getWidth()*2/3,getHeight()/5+i*getHeight()/6);
				g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
			}
		};
		b.addActionListener(e->{
			if(doc.var==null)try{
				doc.var=type.getDeclaredConstructor().newInstance();
				ProgramStarter.editor.constructEditor(doc.var,true);
			}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		});
		int s=tab.getHeight()/2;
		b.setBounds((tab.getWidth()-s)/2,tab.getHeight()/20,s,s);
		tab.add(b);
		HButton confirm=new HButton(){
			public void paint(Graphics g){
				g.setColor(doc.var==null?new Color(50+scale*6,50,50):new Color(50+scale*2,50+scale*6,50));
				g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(doc.var==null?Color.LIGHT_GRAY:Color.GREEN);
				g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		confirm.addActionListener(e->{
			if(doc.var!=null){
				processor.accept(doc.var);
				doc.var=null;
			}
		});
		confirm.setBounds((tab.getWidth()-s)/2,s+tab.getHeight()/15,s,tab.getHeight()/10);
		confirm.setOpaque(false);
		confirm.setText("Подтвердить");
		tab.add(confirm);
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		
	}
	public String toString(){return name;}
}
