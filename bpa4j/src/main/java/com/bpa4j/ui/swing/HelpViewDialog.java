package com.bpa4j.ui.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.bpa4j.core.Root;
import com.bpa4j.navigation.HelpView.CommentInstruction;
import com.bpa4j.navigation.HelpView.ErrorInstruction;
import com.bpa4j.navigation.HelpView.FeatureInstruction;
import com.bpa4j.navigation.HelpView.Instruction;
import com.bpa4j.navigation.HelpView.StartInstruction;
import com.bpa4j.navigation.HelpView.TextInstruction;
import com.bpa4j.navigation.ImplementedInfo;
import com.bpa4j.ui.swing.util.HButton;

public class HelpViewDialog extends JDialog{
	public HelpViewDialog(Window w,ImplementedInfo info){
		super(w,ModalityType.APPLICATION_MODAL);
		setSize(Root.SCREEN_SIZE);
		setUndecorated(true);
		JPanel content=new JPanel();
		CardLayout layout=new CardLayout();
		content.setLayout(layout);
		content.setBorder(BorderFactory.createRaisedBevelBorder());
		setContentPane(content);
		Font font=new Font(Font.DIALOG,Font.PLAIN,getHeight()/20);
		JPanel initial=createInstructionList(info.getChain(),font);
		JLabel l=new JLabel(info.getName());
		l.setBounds(getWidth()/100,getHeight()/20,getWidth()*49/50,getHeight()/20);
		l.setForeground(Color.LIGHT_GRAY);
		l.setFont(font.deriveFont(Font.ITALIC));
		initial.add(l);
		HButton ok=new HButton(){
			public void paint(Graphics g){
				int c=100-scale*5;
				if(getModel().isPressed())c-=50;
				g.setColor(new Color(c,c,c));
				g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(getModel().isPressed()?Color.GREEN:new Color(0,100,0));
				g.drawString("Готово",(getWidth()-fm.stringWidth("Готово"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		ok.addActionListener(e->dispose());
		ok.setBounds(getWidth()/3,getHeight()*9/10,getWidth()/3,getHeight()/20);
		ok.setOpaque(false);
		ok.setFont(font);
		initial.add(ok);
		content.add("init",initial);
		layout.show(content,"init");
		setVisible(true);
	}
	public JPanel createInstructionList(List<Instruction>s,Font font){
		JPanel initial=new JPanel(null);
		initial.setBackground(Color.BLACK);
		JPanel list=new JPanel();
		JScrollPane sList=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		list.setLayout(new GridLayout(0,1));
		list.setPreferredSize(new Dimension(getWidth(),getHeight()*s.size()/7));
		for(Instruction in:s.reversed()){
			list.add(createLabel(font,in));
		}
		initial.add(sList);
		sList.setBounds(0,getHeight()/8,getWidth(),Math.min(list.getPreferredSize().height,getHeight()*3/4));
		return initial;
	}
	private JLabel createLabel(Font font,Instruction in){
		JLabel label=new JLabel(in.toString());
		switch(in){
			case StartInstruction s->{
				label.setFont(font);
				label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(40,204,29)));
			}
			case FeatureInstruction s->{
				label.setFont(font);
				label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(43,214,197)));
			}
			case TextInstruction s->{
				label.setFont(font);
				label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(62,74,92)));
			}
			case CommentInstruction s->{
				label.setFont(font.deriveFont(Font.ITALIC));
				label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
			}
			case ErrorInstruction s->{
				label.setFont(font.deriveFont(Font.BOLD));
				label.setForeground(Color.RED);
			}
			default->throw new UnsupportedOperationException("Instruction of type "+in.getClass()+" is not supported.");
		}
		return label;
	}
}