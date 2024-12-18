package com.futurefactory;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RadialGradientPaint;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.futurefactory.core.ProgramStarter;
import com.futurefactory.core.Root;
import com.futurefactory.core.User;
import com.futurefactory.core.User.Feature;

/**
 * <br>Displays step-by step guide for reaching some functions.</br>
 * <br>Guides are parsed from the {@code helppath.cfg} file.</br>
 * <br>
 * Each line of the file should consist of <i>path</i> and description, separeted with a space.
 * Path contains elements, delimited with a dot.
 * Each element starts with a <i>flag</i> and continues with some text (it depends on the flag choosen).
 * </br>
 * <br>
 * There are 4 flags with the corresponding meaning:
 * <ul>
 * <li>s - "Log in with the designated user."</li>
 * <li>f - "Select the given feature."</li>
 * <li>t - any text</li>
 * <li>c - any darkened italic text</li>
 * </ul>
 * Use underlines instead of spaces and semicolons instead of dots after the flag.
 * </br>
 */
public class HelpView{
	public static HashMap<String,String>paths=new HashMap<>();
	static{
		try(Scanner sc=new Scanner(Root.CL.getResourceAsStream("resources/helppath.cfg"))){
			while(sc.hasNextLine()){
				String[]s=sc.nextLine().split(" ",2);
				paths.put(s[1],s[0]);
			}
		}
	}
	private static class HelpDialog extends JDialog{
		public HelpDialog(Window w,String text){
			super(w,ModalityType.APPLICATION_MODAL);
			HelpDialog th=this;
			setSize(Root.SCREEN_SIZE);
			setUndecorated(true);
			JPanel content=new JPanel();
			CardLayout layout=new CardLayout();
			content.setLayout(layout);
			content.setBorder(BorderFactory.createRaisedBevelBorder());
			setContentPane(content);
			JPanel initial=new JPanel(null);
			initial.setBackground(Color.BLACK);
			JPanel list=new JPanel();
			JScrollPane sList=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			list.setLayout(new GridLayout(0,1));
			String[]t=paths.get(text).split("\\.");
			Font font=new Font(Font.DIALOG,Font.PLAIN,getHeight()/20);
			class L extends JLabel{
				private char type;
				public L(char type,String text){
					this.type=type;
					setText(switch(type){
						case 's'->{
							Wrapper<User>w=new Wrapper<>(null);
							User.forEachUser(u->{if(u.login.equalsIgnoreCase(text.replace('_',' ')))w.var=u;});
							if(w.var==null)throw new IllegalArgumentException("There is no user with login \""+text+"\".");
							yield ProgramStarter.authRequired?"Введите логин и пароль: \""+w.var.login+"\","+w.var.password+".":"Выберите пользователя \""+w.var.login+"\".";
						}
						case 'f'->{
							for(Feature f:User.registeredFeatures)if(f.toString().replace(' ','_').equalsIgnoreCase(text))yield "Перейдите на вкладку \""+f.toString()+"\".";
							throw new IllegalArgumentException("There is no feature with text \""+text+"\".");
						}
						case 't'->text.replace('_',' ').replace(';','.');
						case 'c'->text.replace('_',' ').replace(';','.');
						default->"ОШИБКА!";
					});
					setIcon(switch(type){
						case 'f'->{
							for(Feature f:User.registeredFeatures)if(f.toString().replace(' ','_').equalsIgnoreCase(text)){
								BufferedImage m=new BufferedImage(th.getHeight()/8,th.getHeight()/8,6);
								Graphics2D g2=m.createGraphics();
								g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
								g2.setColor(new Color(0,0,0,150));
								f.paint(g2,m,m.getHeight());
								g2.dispose();
								yield new ImageIcon(m);
							}
							yield null;
						}
						default->null;
					});
					setFont(font);
					Color c=switch(type){
						case 's'->new Color(40,204,29);
						case 'f'->new Color(43,214,197);
						case 't'->new Color(62,74,92);
						default->Color.GRAY;
					};
					setForeground(c);
					setBackground(new Color(c.getRed()/2,c.getGreen()/2,c.getBlue()/2));
				}
				public void paint(Graphics g){
					Graphics2D g2=(Graphics2D)g;
					g2.setColor(Color.DARK_GRAY);
					g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
					g2.setStroke(new BasicStroke(getHeight()/50));
					g2.setColor(Color.GRAY);
					g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
					g2.setPaint(new RadialGradientPaint(getWidth()/100+getHeight()/2,getHeight()/2,getHeight()/3,new float[]{0,1},new Color[]{getForeground(),getBackground()}));
					g2.fillOval(getWidth()/100,getHeight()/4,getHeight()/2,getHeight()/2);
					g2.setColor(new Color(255,255,255,50));
					g2.drawOval(getWidth()/100,getHeight()/4,getHeight()/2,getHeight()/2);
					FontMetrics fm=g2.getFontMetrics();
					switch(type){
						case 's','t'->{
							g2.setColor(Color.WHITE);
							g2.drawString(getText(),getWidth()/50+getHeight()/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						}
						case 'f'->{
							g2.setColor(Color.WHITE);
							// g2.drawString(getText(),getWidth()/50+getHeight()/2,getHeight()/100+fm.getAscent()+fm.getLeading()-fm.getDescent());
							g2.drawString(getText(),getWidth()/50+getHeight()/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
							getIcon().paintIcon(this,g2,getWidth()*9/10-getIcon().getIconWidth(),(getHeight()-getIcon().getIconHeight())/2);
						}
						case 'c'->{
							g2.setFont(g2.getFont().deriveFont(Font.ITALIC));
							g2.setColor(Color.LIGHT_GRAY);
							g2.drawString(getText(),getWidth()/50+getHeight()/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						}
						default->{
							g2.setColor(Color.LIGHT_GRAY);
							g2.drawString(getText(),getWidth()/50+getHeight()/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						}
					}
				}
			}
			list.setPreferredSize(new Dimension(getWidth(),getHeight()*t.length/7));
			for(String s:t){String ss=s.substring(1);list.add(new L(s.charAt(0),ss));}
			initial.add(sList);
			sList.setBounds(0,getHeight()/8,getWidth(),Math.min(list.getPreferredSize().height,getHeight()*3/4));
			JLabel l=new JLabel(text);
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
	}
	public static void show(String name){
		new HelpDialog(Window.getWindows()[0],name);
	}
	public static void showIfFirst(String name){
		File f=new File(Root.folder+"ttentry/"+name);
		if(!f.exists())try{
			f.createNewFile();
			show(name);
		}catch(IOException ex){throw new RuntimeException(ex);}
	}
}
