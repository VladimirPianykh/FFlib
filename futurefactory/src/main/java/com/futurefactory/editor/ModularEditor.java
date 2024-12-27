package com.futurefactory.editor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.editor.modules.FormModule;
import com.futurefactory.editor.modules.EditorModule;
import com.futurefactory.HButton;
import com.futurefactory.PathIcon;
import com.futurefactory.core.ProgramStarter;
import com.futurefactory.core.Root;
import com.futurefactory.core.Data.Editable;

public class ModularEditor implements IEditor{
	public ArrayList<EditorModule>modules=new ArrayList<>();
	public ModularEditor(){modules.add(new FormModule());}
	public ModularEditor(EditorModule...modules){this.modules.addAll(Arrays.asList(modules));}
	public void constructEditor(Editable editable,boolean isNew,Runnable deleter){
		JDialog editor=new JDialog(ProgramStarter.frame,true);
		editor.setSize(Root.SCREEN_SIZE);
		editor.setUndecorated(true);
		editor.setLayout(null);
		CardLayout layout=new CardLayout();
		JPanel mainPanel=new JPanel(layout);
		mainPanel.setBounds(0,0,editor.getWidth(),editor.getHeight());
		PathIcon leftIcon=new PathIcon("ui/left.png",editor.getHeight()/13,editor.getHeight()/13),r=new PathIcon("ui/right.png",editor.getHeight()/13,editor.getHeight()/13);
		HButton left=new HButton(10,7){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				int c=scale*5;
				g2.setColor(new Color(c,pressed?c:c*2,c));
				g2.fillRect(0,0,getWidth(),getHeight());
				leftIcon.paintIcon(this,g2,(getWidth()-leftIcon.getIconWidth())/2,(getHeight()-leftIcon.getIconHeight())/2);
			}
		},right=new HButton(10,7){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				int c=scale*5;
				g2.setColor(new Color(c,pressed?c:c*2,c));
				g2.fillRect(0,0,getWidth(),getHeight());
				r.paintIcon(this,g2,(getWidth()-r.getIconWidth())/2,(getHeight()-r.getIconHeight())/2);
			}
		};
		left.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){layout.previous(mainPanel);editor.repaint();}
		});
		right.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){layout.next(mainPanel);editor.repaint();}
		});
		left.setBounds(editor.getWidth()/100,editor.getHeight()*4/5,editor.getHeight()/10,editor.getHeight()/10);
		right.setBounds(editor.getWidth()*99/100-editor.getHeight()/10,editor.getHeight()*4/5,editor.getHeight()/10,editor.getHeight()/10);
		left.setFocusable(false);
		right.setFocusable(false);
		editor.add(left);editor.add(right);
		editor.add(mainPanel);
		int k=1;
		for(EditorModule m:modules){
			JPanel tab=m.createTab(editor,editable,isNew,deleter);
			if(tab!=null){
				mainPanel.add(tab,"tab"+k);
				++k;
			}
		}
		if(k==1)return;
		else if(k==2){
			left.setVisible(false);
			right.setVisible(false);
		}
		layout.show(mainPanel,"tab1");
		editor.setVisible(true);
    }
}
