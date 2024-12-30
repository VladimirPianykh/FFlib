package com.bpa4j.editor.modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.bpa4j.HButton;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.Editable.ActionRecord;

public class LogWatchModule implements EditorModule{
    public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		JPanel tab=new JPanel(null);
		tab.setBackground(new Color(102,107,89));
		JPanel history=new JPanel(null);
		history.setPreferredSize(new Dimension(editor.getWidth(),editor.getHeight()*editable.records.size()/4));
		history.setOpaque(false);
		JScrollPane s=new JScrollPane(history,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.setSize(editor.getWidth(),editor.getHeight()*3/4);
		s.getViewport().setBackground(new Color(19,31,19));
		s.getVerticalScrollBar().setOpaque(false);
		s.getVerticalScrollBar().setUnitIncrement(editor.getHeight()/30);
		tab.add(s);
		class LocalComment extends HButton{
			private static final float[]f={0,0.4f,0.5f,0.6f,1};
			private static final Color[]c1={new Color(44,66,65),new Color(47,77,75),new Color(58,97,94),new Color(47,77,75),new Color(44,66,65)};
			private static final Color[]c2={new Color(68,71,71),new Color(79,84,84),new Color(82,92,92),new Color(87,94,94),new Color(68,71,71)};
			private final BasicStroke stroke=new BasicStroke(editor.getHeight()/150);
			private ActionRecord s;
			public LocalComment(ActionRecord s,int index){
				super(15,5);
				setBounds(editor.getWidth()/10,index*editor.getHeight()/4+editor.getHeight()/40,editor.getWidth()*4/5,editor.getHeight()/5);
				this.s=s;
				setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.text),null);
					}
				});
			}
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				int h=getHeight()/10;
				g2.setPaint(new LinearGradientPaint(0,0,getHeight()*2,getHeight(),f,c2));
				g2.fillRoundRect(0,0,getWidth(),getHeight(),h,h);
				g2.setPaint(new LinearGradientPaint(0,0,getHeight()*2,getHeight(),f,c1));
				g2.setStroke(stroke);
				g2.drawRoundRect(0,0,getWidth(),getHeight(),h,h);
				if(s.text.charAt(0)==':'){
					String t=s.text.split(":")[1];
					switch(t){
						case "CREATED","DELETED":
							g2.setColor(t.equals("CREATED")?new Color(81,122,40):new Color(148,55,9));
							g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/2));
							FontMetrics fm=g2.getFontMetrics();
							String str=t.equals("CREATED")?"СОЗДАНО":"УДАЛЕНО";
							g2.drawString(str,(getWidth()-fm.stringWidth(str))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
							Shape s=g2.getFont().createGlyphVector(g2.getFontRenderContext(),str).getOutline((getWidth()-fm.stringWidth(str))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
							g2.setStroke(new BasicStroke(getHeight()/50));
							g2.setColor(new Color(255,255,255,100));
							g2.draw(s);
							g2.setStroke(new BasicStroke());
							g2.draw(s);
							// g2.setColor(new Color(15,23,12));
							// String t=s.text.substring(s.text.split(":")[1].length()+2,s.text.length());
							// g2.setFont(new Font(Font.MONOSPACED,Font.BOLD,getHeight()/2));
							// fm=g2.getFontMetrics();
							// g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						break;
					}
				}else if(s.text.charAt(0)=='+'){
					g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/4));
					FontMetrics fm=g2.getFontMetrics();
					g2.setColor(Color.GREEN);
					String str="Стадия "+s.text.substring(1)+" успешно завершена";
					g2.drawString(str,(getWidth()-fm.stringWidth(str))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				}else if(s.text.charAt(0)=='-'){
					g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/4));
					FontMetrics fm=g2.getFontMetrics();
					g2.setColor(Color.RED);
					String str="Откачено до стадии "+s.text.substring(1);
					g2.drawString(str,(getWidth()-fm.stringWidth(str))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				}else if(s.text.charAt(0)=='>'){
					g2.setColor(new Color(87,31,14));
					g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/4));
					g2.drawString(s.text.split(":")[0],getWidth()/100,getHeight()-getHeight()/100);
					g2.setColor(Color.BLACK);
					String[]t=s.text.substring(s.text.split(":")[0].length()+1,s.text.length()).split("\n");
					g2.setFont(new Font(Font.MONOSPACED,Font.BOLD,getHeight()/(t.length*2)));
					FontMetrics fm=g2.getFontMetrics();
					for(int i=0;i<t.length;i++)g2.drawString(t[i],(getWidth()-fm.stringWidth(t[i]))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2+fm.getHeight()*(i-t.length/2));
				}
				g2.setColor(new Color(196,196,181));
				g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/10));
				FontMetrics fm=g2.getFontMetrics();
				String time=s.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				g2.drawString(time,getWidth()-(getHeight()/50+fm.stringWidth(time)),getHeight()*49/50);
				if(scale!=0){
					g2.setColor(new Color(0,0,0,scale*5));
					g2.fillRoundRect(0,0,getWidth(),getHeight(),h,h);
					g2.setColor(new Color(255,255,255,scale*10));
					g2.drawString("Нажмите, чтобы скопировать.",(getWidth()-fm.stringWidth("Нажмите, чтобы скопировать."))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())*3/4);
				}
			}
		}
		int i=0;
		if(editable.records.isEmpty()){
			JLabel l=new JLabel("Записи пусты.");
			l.setBounds(0,0,editor.getWidth(),editor.getHeight());
			l.setFont(new Font(Font.DIALOG,Font.BOLD,editor.getHeight()/10));
			history.add(l);
		}else for(ActionRecord c:editable.records){
			history.add(new LocalComment(c,i));
			i++;
		}
		JLabel tab2Name=new JLabel("Записи");
		tab2Name.setBounds(0,editor.getHeight()*9/10,editor.getWidth(),editor.getHeight()/10);
		tab2Name.setForeground(Color.BLACK);
		tab2Name.setFont(new Font(Font.DIALOG,Font.BOLD,editor.getHeight()/40));
		tab2Name.setHorizontalAlignment(JLabel.CENTER);
		tab.add(tab2Name);
		return tab;
	}
}
