package com.bpa4j.navigation;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.bpa4j.Wrapper;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Feature;
import com.bpa4j.core.User.Role;
import com.bpa4j.ui.HButton;

/**
 * <p>Displays Instruction-by Instruction guide for reaching some functions.</p>
 * <p>Guides are parsed from the {@code helppath.cfg} file.</p>
 * <p>
 * Each line of the file should consist of <i>path</i> and description, separeted with a space.
 * Path contains elements, delimited with a dot.
 * Each element starts with a <i>flag</i> and continues with some text (it depends on the flag choosen).
 * </p>
 * <p>
 * There are 4 flags with the corresponding meaning:
 * <ul>
 * <li>s - "Log in with the designated user."</li>
 * <li>f - "Select the given feature."</li>
 * <li>t - any text</li>
 * <li>c - any darkened italic text</li>
 * </ul>
 * Use underlines instead of spaces and semicolons instead of dots after the flag.
 */
public final class HelpView{
	public static interface Instruction{
		JLabel createLabel(Font font);
		char getType();
	}
	public static class StartInstruction implements Instruction{
		private final User user;
		public StartInstruction(User user){
			this.user=user;
		}
		public JLabel createLabel(Font font){
			JLabel label=new JLabel();
			label.setFont(font);
			label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(40,204,29)));
			label.setText(ProgramStarter.authRequired?"Введите логин и пароль: \""+user.login+"\","+user.password+".":"Выберите пользователя \""+user.login+"\".");
			return label;
		}
		public char getType(){
			return 's';
		}
	}
	public static class FeatureInstruction implements Instruction{
		private final Feature feature;
		public FeatureInstruction(Feature feature){
			this.feature=feature;
		}
		public JLabel createLabel(Font font){
			JLabel label=new JLabel("Перейдите на вкладку \""+feature.toString()+"\".");
			label.setFont(font);
			label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(43,214,197)));
			return label;
		}
		public char getType(){
			return 'f';
		}
	}
	public static class TextInstruction implements Instruction{
		private final String text;
		public TextInstruction(String text) {
			this.text = text;
		}
		public JLabel createLabel(Font font){
			JLabel label=new JLabel(text);
			label.setFont(font);
			label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(62,74,92)));
			return label;
		}
		public char getType(){
			return 't';
		}
	}
	public static class CommentInstruction implements Instruction{
		private final String text;
		public CommentInstruction(String text){
			this.text=text;
		}
		public JLabel createLabel(Font font){
			JLabel label=new JLabel(text);
			label.setFont(font.deriveFont(Font.ITALIC));
			label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
			return label;
		}
		public char getType(){
			return 'c';
		}
	}
	public static class ErrorInstruction implements Instruction{
		public ErrorInstruction(){}
		public JLabel createLabel(Font font){
			JLabel label=new JLabel("<навигатор потерялся>");
			label.setFont(font.deriveFont(Font.BOLD));
			label.setForeground(Color.RED);
			return label;
		}
		public char getType(){
			return 'e';
		}
	}
	private HelpView(){}
	public static ArrayList<ImplementedInfo>paths=new ArrayList<>();
	static{
		loadNavigationFromTaskLocs();
		loadNavigationFromFile();
	}
	private static class HelpDialog extends JDialog{
		public HelpDialog(Window w,ImplementedInfo info){
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
				list.add(in.createLabel(font));
			}
			initial.add(sList);
			sList.setBounds(0,getHeight()/8,getWidth(),Math.min(list.getPreferredSize().height,getHeight()*3/4));
			return initial;
		}
	}
	public static List<Instruction>parse(String line){
		ArrayList<Instruction>list=new ArrayList<>();
		for(String in:line.split("\\.")){
			String[]s=in.split(" ",2);
			char type=s[0].charAt(0);
			String text=in.length()==1?"":s[1];
			switch(type){
				case 's'->{
					Wrapper<User>w=new Wrapper<>(null);
					User.forEachUser(u->{if(u.login.equalsIgnoreCase(text.replace('_',' ')))w.var=u;});
					if(w.var==null)throw new IllegalArgumentException("There is no user with login \""+text+"\".");
					list.add(new StartInstruction(w.var));
				}
				case 'f'->{
					boolean found = false;
					for(Feature f:User.registeredFeatures)if(f.toString().replace(' ','_').equalsIgnoreCase(text)){
						list.add(new FeatureInstruction(f));
						found = true;
						break;
					}
					if(!found)throw new IllegalArgumentException("There is no feature with text \""+text+"\".");
				}
				case 't'->list.add(new TextInstruction(text.replace('_',' ').replace(';','.')));
				case 'c'->list.add(new CommentInstruction(text.replace('_',' ').replace(';','.')));
				default->list.add(new ErrorInstruction());
			}
		}
		return list;
	}
	public static void show(ImplementedInfo info){
		new HelpDialog(Window.getWindows()[0],info);
	}
	public static void showIfFirst(ImplementedInfo info){
		File f=new File(Root.folder+"ttentry/"+info);
		if(!f.exists())try{
			f.createNewFile();
			show(info);
		}catch(IOException ex){throw new UncheckedIOException(ex);}
	}
	public static void loadNavigationFromTaskLocs(){
		HashSet<ImplementedInfo>s=new HashSet<>();
		for(Role r:User.registeredRoles)s.addAll(r.getImplementedInfo());
		paths.addAll(s);
	}
	public static void loadNavigationFromFile(){
		try(Scanner sc=new Scanner(Root.getResourceAsStream("resources/helppath.cfg"))){
			while(sc.hasNextLine()){
				String[]s=sc.nextLine().split(" ",2);
				paths.add(new ImplementedInfo(s[1],parse(s[0])));
			}
		}catch(NullPointerException ex){
			System.err.println("Navigation file (helppath.cfg) not found.");
		}
	}
}
