package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import com.bpa4j.Dater;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer;
import com.bpa4j.ui.swing.util.HButton;

public class SwingDatedListRenderer<T extends Editable> implements FeatureRenderer<DatedList<T>>{
	private DatedList<T> contract;
	public SwingDatedListRenderer(DatedList<T> contract){
		this.contract=contract;
	}
	public DatedList<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		SwingFeatureRenderingContext swingCtx=(SwingFeatureRenderingContext)ctx;
		JPanel content=swingCtx.getTarget();
		JPanel tab=new JPanel(null);
		tab.setSize(content.getSize());
		Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/30);
		fillTab(content,tab,font);
		content.add(tab);
		content.revalidate();
	}
	public void renderPreview(FeatureRenderingContext ctx){
		SwingPreviewRenderingContext previewCtx=(SwingPreviewRenderingContext)ctx;
		BufferedImage image=previewCtx.getPreviewTarget();
		int size=image.getHeight();
		Graphics2D g2=image.createGraphics();
		paint(g2,image,size);
		g2.dispose();
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/20));
		g2.drawRoundRect(h/10,h/10,h*4/5,h*4/5,h/10,h/10);
		g2.setClip(new RoundRectangle2D.Double(h/10,h/10,h*4/5,h*4/5,h/10,h/10));
		g2.setStroke(new BasicStroke(h/50));
		for(int x=h/10;x<h*9/10;x+=h/10)
			g2.drawLine(x,h/10,x,h*9/10);
		for(int y=h/10;y<h*9/10;y+=h/10)
			g2.drawLine(h/10,y,h*9/10,y);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new GridLayout());
		JPanel panel=new JPanel(new GridLayout(Math.max(10,contract.getObjects().size()+1),1));
		JScrollPane s=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		for(T t:contract.getObjects()){
			JComponent p=createTableEntry(t,tab,font);
			panel.add(p);
		}
		HButton add=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed()) c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.WHITE);
				FontMetrics fm=g.getFontMetrics();
				g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		add.addActionListener(e->{
			T t=contract.createObject();
			panel.add(createTableEntry(t,tab,font),panel.getComponentCount()-1);
			ProgramStarter.editor.constructEditor(t,true,()->contract.removeObject(t),null);
			panel.revalidate();
		});
		add.setFont(font);
		panel.add(add);
		tab.add(s);
	}
	@SuppressWarnings("null")
	private JComponent createTableEntry(T t,JPanel tab,Font font){
		Wrapper<LocalDate> date=new Wrapper<>(LocalDate.now());
		var objectsWithDaters=contract.getObjectsWithDaters();
		Dater<T> dater=objectsWithDaters.get(t);
		if(dater==null){
			if(contract.getDateProvider()!=null){
				dater=contract.getDateProvider().get();
				contract.putObject(t,dater);
			}
		}
		final Dater<T> finalDater=dater;
		Field[] fields=dater!=null?dater.getClass().getFields():new Field[0];
		boolean flag=false;
		for(Field f:fields)
			if(f.isAnnotationPresent(EditorEntry.class)){
				flag=true;
				break;
			}
		ArrayList<Class<? extends EditorEntryBase>> editors=null;
		if(flag){
			editors=new ArrayList<>();
			for(Field f:fields)
				editors.add(f.getAnnotation(EditorEntry.class).editorBaseSource());
		}
		JPanel p=new JPanel(null);
		p.setBackground(Color.DARK_GRAY);
		HButton b=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*3;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(t.name,(getWidth()-fm.stringWidth(t.name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		b.addActionListener(e->{
			ProgramStarter.editor.constructEditor(t,false,()->contract.removeObject(t),null);
			tab.revalidate();
		});
		b.setSize(tab.getWidth()/3,tab.getHeight()/10);
		b.setFont(new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/20));
		p.add(b);
		if(finalDater!=null){
			JPanel dates=new JPanel(new GridLayout(1,7));
			dates.setBounds(tab.getWidth()/3,0,flag?tab.getWidth()/2:tab.getWidth()*2/3,tab.getHeight()/10);
			for(int i=0;i<7;++i)
				dates.add(finalDater.apply(t,date.var.plusDays(i)));
			b.addMouseWheelListener(e->{
				date.var=date.var.plusDays(e.getWheelRotation());
				dates.removeAll();
				for(int i=0;i<7;++i)
					dates.add(finalDater.apply(t,date.var.plusDays(i)));
			});
			p.add(dates);
		}
		if(flag&&finalDater!=null) try{
			JPopupMenu menu=new JPopupMenu("Параметры:");
			ArrayList<java.util.function.Supplier<?>> savers=new ArrayList<>();
			for(int i=0;i<fields.length;++i){
				Wrapper<java.util.function.Supplier<?>> saver=new Wrapper<>(null);
				JPanel c=new JPanel(new GridLayout());
				c.setPreferredSize(new Dimension(tab.getWidth()/6,tab.getHeight()/15));
				c.setBorder(BorderFactory.createTitledBorder(fields[i].getAnnotation(EditorEntry.class).translation()));
				if(editors.get(i)==EditorEntryBase.class) c.add(SwingFormModuleRenderer.createEditorBase(finalDater,fields[i],saver));
				else c.add(editors.get(i).getDeclaredConstructor().newInstance().createEditorBase(finalDater,fields[i],saver,new Wrapper<>(null)));
				menu.add(c);
				savers.add(saver.var);
			}
			JButton params=new JButton("Параметры");
			params.addActionListener(e->menu.show(p.getTopLevelAncestor(),params.getLocationOnScreen().x,params.getLocationOnScreen().y));
			params.setBounds(tab.getWidth()*5/6,0,tab.getWidth()/6,tab.getHeight()/10);
			params.setFont(font);
			params.setForeground(Color.BLACK);
			menu.addPopupMenuListener(new PopupMenuListener(){
				public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e){
					try{
						for(int i=0;i<fields.length;++i)
							fields[i].set(finalDater,savers.get(i).get());
					}catch(IllegalAccessException ex){
						throw new IllegalStateException(ex);
					}
				}
				public void popupMenuCanceled(PopupMenuEvent e){}
			});
			p.add(params);
		}catch(IllegalAccessException ex){
			throw new IllegalStateException("The class with editable fields cannot be anonimous. These fields must be public.",ex);
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException(ex);
		}
		return p;
	}
}
