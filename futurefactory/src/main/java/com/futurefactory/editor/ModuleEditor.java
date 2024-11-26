package com.futurefactory.editor;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.futurefactory.Data.Editable;
import com.futurefactory.HButton;
import com.futurefactory.PathIcon;
import com.futurefactory.ProgramStarter;
import com.futurefactory.Root;
import com.futurefactory.Wrapper;

public class ModuleEditor implements IEditor{
	public ArrayList<IEditorModule>modules=new ArrayList<>();
	public ModuleEditor(){modules.add(new FormModule());}
	public void constructEditor(Editable editable){
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
		JButton ok=new JButton(){
			public void paint(Graphics g){
				g.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight(),getHeight()));
				g.setColor(getModel().isPressed()?Color.DARK_GRAY:Color.GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.BLACK);
				FontMetrics fm=g.getFontMetrics();
				g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				if(getModel().isRollover()){
					g.setColor(new Color(255,255,255,200));
					((Graphics2D)g).setStroke(new BasicStroke(getHeight()/10));
					g.drawRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
				}
			}
		};
		ok.setBounds(editor.getWidth()*2/5,editor.getHeight()*9/10,editor.getWidth()/5,editor.getHeight()/20);
		ok.setOpaque(false);
		ok.setText("Готово");
		ok.setFont(new Font(Font.DIALOG,Font.PLAIN,ok.getHeight()));
		JTextField nameField=new JTextField(editable.name);
		nameField.setBounds(editor.getWidth()/5,editor.getHeight()/100,editor.getWidth()*3/5,editor.getHeight()/10);
		nameField.setFont(new Font(Font.DIALOG,Font.PLAIN,nameField.getHeight()*2/3));
		nameField.setBackground(Color.DARK_GRAY);
		nameField.setForeground(Color.LIGHT_GRAY);
		Wrapper<Verifier>verifier=new Wrapper<Verifier>(null);
		VerifiedInput v=((VerifiedInput)editable.getClass().getAnnotation(VerifiedInput.class));
		if(v!=null)try{verifier.var=v.verifier().getDeclaredConstructor().newInstance();}catch(Exception ex){throw new RuntimeException(ex);}
		ok.addActionListener(e->{
			if(ok.isFocusOwner()&&(verifier.var==null||verifier.var.verify(editable))){editor.dispose();editable.name=nameField.getText();}
		});
		int k=1;
		for(IEditorModule m:modules){
			JPanel tab=m.createTab(editor,editable,nameField,ok);
			mainPanel.add(tab,"tab"+k);
		}
		// mainPanel.add(tab1,"tab1");
		//TODO: add every module
		//Add them as shown below.
		layout.show(mainPanel,"tab1");
		nameField.requestFocusInWindow();
		nameField.setSelectionStart(0);
		nameField.setSelectionEnd(nameField.getText().length());
		editor.setVisible(true);
    }
}
