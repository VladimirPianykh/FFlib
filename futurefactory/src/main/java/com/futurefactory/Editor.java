package com.futurefactory;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Toolkit;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.futurefactory.Data.Editable;
import com.futurefactory.User.Role;

class Editor extends JDialog{
	public Editor(Editable editable){
		super(Main.frame,true);
		setSize(Root.SCREEN_SIZE);
		setUndecorated(true);
		CardLayout layout=new CardLayout();
		setContentPane(new JPanel(layout));
		boolean isNew=editable==null||(editable.getStage()==Role.ENGINEER&&User.getActiveUser().role==Role.ENGINEER),
		access=isNew||(editable!=null&&editable.getStage()==User.getActiveUser().role);
		Editable m=editable==null?new Editable("Editable","",0,new HashMap<Part,Integer>()):editable;
		int n=m==null?0:m.parts.values().stream().mapToInt(a->{return a;}).sum();
		JPanel tab1=new JPanel(null),tab2=new JPanel(null),tab3=new JPanel(null);
		JPanel table=new JPanel(new GridLayout(n,5));
		table.setPreferredSize(new Dimension(getWidth(),getHeight()*n/10));
		table.setSize(table.getPreferredSize());
		JScrollPane s=new JScrollPane(table);
		s.setSize(getWidth(),Math.min(getHeight()*7/10,table.getHeight()));
		s.getVerticalScrollBar().setUnitIncrement(getHeight()/30);
		tab1.add(s);
		Font font=new Font(Font.DIALOG,Font.BOLD,getHeight()/40);
		for(Part r:m.parts.keySet())for(int i=0;i<m.parts.get(r);i++){
			JLabel[]data={new JLabel(r.name),new JLabel(r.article),new JLabel(r.weight+"g"),new JLabel(r.date.toString()),new JLabel(r.type.name())};
			for(JLabel l:data){
				l.setFont(font);
				l.setBackground(Color.GRAY);
				l.setForeground(Color.BLACK);
				if(User.getActiveUser().role!=Role.TESTER&&access)l.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						if(SwingUtilities.isRightMouseButton(e)){
							if(m.parts.get(r)<2)m.parts.remove(r);
							else m.parts.put(r,m.parts.get(r)-1);
							for(JLabel d:data){
								d.setText("<deleted>");
								d.setEnabled(false);
								Saver.saveModels();
							}
						}
					}
				});
				table.add(l);
			}
		}
		int h;
		JDialog th=this;
		if(User.getActiveUser().role!=Role.TESTER&&access){
			BufferedImage addImage=new BufferedImage(getHeight()/10,getHeight()/10,6);
			Graphics2D g2=addImage.createGraphics();
			h=getHeight()/10;
			g2.setPaint(new RadialGradientPaint(h/2,h/2,h/2,new float[]{0,1},new Color[]{new Color(94,86,82),new Color(66,60,57)}));
			g2.fillRect(0,0,h,h);
			g2.setStroke(new BasicStroke(h/40,2,2));
			g2.setPaint(new RadialGradientPaint(h/2,h/2,h/2,new float[]{0,1},new Color[]{new Color(110,128,40),new Color(81,92,36)}));
			g2.drawRect(h/3,h/10,h/3,h*4/5);
			g2.drawRect(h/10,h/3,h*4/5,h/3);
			HButton add=new HButton(){
				public void paintComponent(Graphics g){
					g.setColor(Color.GRAY);
					g.fillRect(0,0,getWidth(),getHeight());
					g.drawImage(addImage,0,0,this);
					g.setColor(new Color(0,0,0,scale*10));
					g.fillRect(0,0,getWidth(),getHeight());
				}
			};
			add.setBounds(0,s.getHeight(),getWidth(),getHeight()/10);
			add.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					add.setEnabled(false);
					new Thread(){
						public void run(){
							JDialog adder=new JDialog(th,true);
							int w=getWidth()*4/5,h=getHeight()*4/5;
							Collection<Part>parts=Part.partMap.values();
							JPanel p=new JPanel(new GridLayout(parts.size(),1));
							p.setPreferredSize(new Dimension(w,h*parts.size()/7));
							p.setSize(p.getPreferredSize());
							JScrollPane f=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							f.setSize(w,Math.min(p.getHeight(),h));
							f.getVerticalScrollBar().setUnitIncrement(h/50);
							adder.setContentPane(new JPanel(new GridLayout(1,1)){
								public void paintComponent(Graphics g){
									Graphics2D g2=(Graphics2D)g;
									g2.setPaint(new RadialGradientPaint(getWidth()*3/4,getHeight()*9/10,getHeight(),new float[]{0,0.1f,1},new Color[]{new Color(97,97,79),new Color(69,60,48),new Color(51,46,35)}));
									g2.fillRect(0,0,getWidth(),getHeight());
									Painter.lightInterBorder(g2,w,h,new Color(64,37,26,50),10);
								}
							});
							adder.setUndecorated(true);
							adder.setBounds(getWidth()/10,getHeight()/10,getWidth()*4/5,getHeight()*4/5);
							Wrapper<Part>r=new Wrapper<Part>(null);
							adder.add(f);
							for(Part k:parts){
								HButton b=new HButton(){
									public void paintComponent(Graphics g){
										Graphics2D g2=(Graphics2D)g;
										g2.setColor(Color.GRAY);
										g2.fillRect(0,0,getWidth(),getHeight());
										g2.setFont(font);
										FontMetrics fm=g2.getFontMetrics();
										g2.setPaint(new GradientPaint(0,0,new Color(20,20,20),0,getHeight(),Color.BLACK));
										g2.drawString(getText(),getWidth()/100,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
										g2.setColor(new Color(0,0,0,scale*10));
										g2.fillRect(0,0,getWidth(),getHeight());
									}
								};
								b.setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent e){
										r.var=k;
										adder.dispose();
									}
								});
								b.setText(k.name);
								b.setFont(new Font(Font.DIALOG,Font.PLAIN,h/20));
								p.add(b);
							}
							adder.setVisible(true);
							JLabel[]data={new JLabel(r.var.name),new JLabel(r.var.article),new JLabel(r.var.weight+"g"),new JLabel(r.var.date.toString()),new JLabel(r.var.type.name())};
							if(m.parts.containsKey(r.var))m.parts.put(r.var,m.parts.get(r.var)+1);
							else m.parts.put(r.var,1);
							for(JLabel l:data){
								l.setFont(font);
								l.setBackground(Color.GRAY);
								l.setForeground(Color.BLACK);
								l.addMouseListener(new MouseAdapter(){
									public void mouseClicked(MouseEvent e){
										if(SwingUtilities.isRightMouseButton(e)){
											if(m.parts.get(r.var)<2)m.parts.remove(r.var);
											else m.parts.put(r.var,m.parts.get(r.var)-1);
											for(JLabel d:data){
												d.setText("<deleted>");
												d.setEnabled(false);
												Saver.saveModels();
											}
										}
									}
								});
								table.add(l);
							}
							int n=((GridLayout)table.getLayout()).getRows()+1;
							table.setLayout(new GridLayout(n,5));
							table.setPreferredSize(new Dimension(getWidth(),getHeight()*n/10));
							table.setSize(table.getPreferredSize());
							s.setSize(getWidth(),Math.min(getHeight()*7/10,table.getHeight()));
							s.revalidate();
							add.setBounds(0,s.getHeight(),getWidth(),getHeight()/10);
							Saver.saveModels();
							add.setEnabled(true);
						}
					}.start();
				}
			});
			tab1.add(add);
		}
		float[]fr={0,1};
		Color c2=new Color(0,0,0,50),c3=new Color(0,0,0,20),c4=new Color(0,0,0,0),c5=new Color(66,66,71),c6=new Color(42,42,46),c7=new Color(143,133,103),c8=new Color(120,113,92),c9=new Color(58,58,64),c10=new Color(125,125,158),c11=new Color(82,82,105);
		String t=isNew?"SUBMIT":access?"APPROVE":"OK";
		HButton submit=new HButton(45,2){
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setColor(c5);
				RoundRectangle2D.Double r1=new RoundRectangle2D.Double(getWidth()*scale/(maxScale*40),getHeight()*scale/(maxScale*40),(int)(getWidth()/((float)scale/(maxScale*20)+1)),(int)(getHeight()/((float)scale/(maxScale*20)+1)),getHeight()/10,getHeight()/10);
				g2.setClip(r1);
				g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
				AffineTransform a=g2.getTransform();
				g2.rotate(Math.toRadians(45-Math.min(45,scale*3)),getWidth()/2,getHeight()/2);
				RoundRectangle2D.Double r2=new RoundRectangle2D.Double(getWidth()/3-Math.max(0,(scale-30))*getWidth()/90,getHeight()/6,getWidth()/3+Math.max(0,(scale-30))*getWidth()/45,getHeight()*2/3,getHeight()/10,getHeight()/10);
				g2.setColor(c6);
				g2.fill(r2);
				g2.setClip(r2);
				g2.setColor(c2);
				g2.setStroke(new BasicStroke((scale+100)*getHeight()/1800));
				g2.draw(r2);
				g2.setTransform(a);
				g2.setClip(r1);
				g2.setStroke(new BasicStroke(getHeight()/50));
				g2.setColor(c6);
				g2.drawRoundRect(getWidth()*scale/(maxScale*40)+1,getHeight()*scale/(maxScale*40)+1,(int)(getWidth()/((float)scale/(maxScale*20)+1))-2,(int)(getHeight()/((float)scale/(maxScale*20)+1))-2,getHeight()/10,getHeight()/10);
				g2.setFont(new Font(Font.DIALOG_INPUT,Font.PLAIN,getHeight()/4));
				FontMetrics fm=g2.getFontMetrics();
				g2.setPaint(new LinearGradientPaint(getWidth()/2,0,getWidth()/2,getHeight(),fr,new Color[]{new Color(85+48*scale/maxScale,83+48*scale/maxScale,23+14*scale/maxScale),new Color(77+58*scale/maxScale,75+58*scale/maxScale,14+23*scale/maxScale)}));
				g2.drawString(t.substring(0,scale*(t.length()-1)/45+1),(getWidth()-fm.stringWidth(t.substring(0,scale*(t.length()-1)/45+1)))/2,(getHeight()+fm.getAscent()-fm.getDescent()-fm.getLeading())/2);
				g2.setPaint(new GradientPaint(getWidth()*3/4,getHeight()/2,c3,getWidth()*3/4-getHeight()/100,getHeight()/2-getHeight()/50,c4));
				g2.fill(r1);
			}
		};
		submit.setBounds(getWidth()/(access&&!isNew?3:2)-getHeight()/10,getHeight()-getHeight()/9,getHeight()/5,getHeight()/10);
		Font nFont=new Font(Font.SANS_SERIF,Font.PLAIN,getHeight()/5);
		JTextField name=new JTextField(){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setFont(nFont);
				FontMetrics fm=g2.getFontMetrics();
				g2.setPaint(new GradientPaint(0,0,c7,getWidth(),getHeight(),c8));
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setPaint(new LinearGradientPaint(0,0,getWidth()/2,getHeight(),new float[]{0.4f,1},new Color[]{c11,c10},CycleMethod.REFLECT));
				g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,fm.getAscent()+fm.getLeading()-fm.getDescent());
				g2.setPaint(new LinearGradientPaint(0,0,getHeight(),getHeight(),new float[]{0,1},new Color[]{c9,new Color(30,30,30)},CycleMethod.REFLECT));
				g2.setStroke(new BasicStroke(getHeight()/30));
				g2.draw(nFont.createGlyphVector(g2.getFontRenderContext(),getText()).getOutline((getWidth()-fm.stringWidth(getText()))/2,fm.getAscent()+fm.getLeading()-fm.getDescent()));
			}
		},article=new JTextField(),weight=new JTextField(){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				g.setColor(new Color(69,74,24));
				FontMetrics fm=g.getFontMetrics();
				g.drawString("g",getWidth()-fm.charWidth('g')*2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		name.setBounds(getWidth()/6,getHeight()/100,getWidth()*2/3,getHeight()/6);
		article.setBounds(getWidth()/12,getHeight()/6+getHeight()/50,getWidth()/3,getHeight()/10);
		weight.setBounds(getWidth()*7/12,getHeight()/6+getHeight()/50,getWidth()/3,getHeight()/10);
		article.setFont(font);weight.setFont(font);
		if(editable!=null){
			name.setText(editable.name);
			article.setText(editable.article);
			weight.setText(String.valueOf(editable.weight));
		}
		tab2.add(submit);
		tab2.add(name);tab2.add(article);tab2.add(weight);
		if(!access||User.getActiveUser().role==Role.TESTER)for(Component c:tab2.getComponents())c.setFocusable(false);
		class LocalButton extends HButton{
			Color[]c1;
			public LocalButton(Color[]colors){
				super(45,2);
				c1=colors;
			}
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				AffineTransform a=g2.getTransform();
				a.scale(1-scale/(maxScale*5d),1-scale/(maxScale*5d));
				a.translate(getWidth()*scale/(maxScale*10),getHeight()*scale/(maxScale*10));
				a.rotate(Math.toRadians(scale*8),getWidth()/2,getHeight()/2);
				g2.setTransform(a);
				g2.setPaint(new RadialGradientPaint(getWidth()*2/5,getHeight()*2/5,getHeight()/2,new float[]{0,0.4f,1},c1));
				g2.fillOval(0,0,getWidth(),getHeight());
				g2.setPaint(new RadialGradientPaint(getWidth()/2,getHeight()/2,getHeight()/2,new float[]{0.95f,1},new Color[]{new Color(0,0,0,0),new Color(0,0,0,40)}));
				g2.fillOval(0,0,getWidth(),getHeight());
			}
		}
		LocalButton next1=new LocalButton(new Color[]{new Color(135,158,60),new Color(99,120,32),new Color(75,92,21)}),
		next2=new LocalButton(next1.c1),next3=new LocalButton(next1.c1);
		next1.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){layout.next(getContentPane());}
		});
		next2.setAction(next1.getAction());next3.setAction(next1.getAction());
		next1.setBounds(getWidth()/100,getHeight()*4/5,getHeight()/10,getHeight()/10);
		next2.setBounds(next1.getBounds());next3.setBounds(next1.getBounds());
		tab1.add(next1);tab2.add(next2);tab3.add(next3);
		if(editable!=null){
			JComponent scheme=editable.createScheme();
			scheme.setBounds(getWidth()/4,getHeight()/2-getHeight()/8,getWidth()/2,getHeight()/4);
			tab2.add(scheme);
		}
		AbstractAction cancel=new AbstractAction(){
			public void actionPerformed(ActionEvent e){dispose();}
		};
		if(access){
			h=getHeight()/10;
			int f=h/20;
			Polygon p=new Polygon(new int[]{h/4,h/4+f,h/2,h*3/4-f,h*3/4,h/2+f,h*3/4,h*3/4-f,h/2,h/4+f,h/4,h/2-f},new int[]{h/4+f,h/4,h/2-f,h/4,h/4+f,h/2,h*3/4-f,h*3/4,h/2+f,h*3/4,h*3/4-f,h/2},12);
			LocalButton c=new LocalButton(new Color[]{new Color(181,87,58),new Color(128,51,28),new Color(107,44,26)}){
				public void paint(Graphics g){
					super.paint(g);
					g.setColor(Color.BLACK);
					g.drawPolygon(p);
				}
			};
			c.setBounds(getWidth()*9/10-h,h*8-h,h,h);
			c.setAction(cancel);
			tab2.add(c);
			if(isNew)submit.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					if(name.getText().isBlank()||article.getText().isBlank()||weight.getText().isBlank())return;
					try{
						m.weight=Integer.parseInt(weight.getText());
						m.name=name.getText();
						m.article=article.getText();
						if(editable==null)Editable.modelMap.putIfAbsent(m.name,m);
						else{
							m.nextStage(User.getActiveUser());
							Editable.modelMap.put(m.name,m);
						}
						dispose();
						Saver.saveModels();
					}catch(NumberFormatException ex){}
				}
			});
			else{
				submit.setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						if(name.getText().isBlank()||article.getText().isBlank()||weight.getText().isBlank())return;
						submit.setEnabled(false);
						new Thread(){
							public void run(){
								Wrapper<Boolean>b1=new Wrapper<Boolean>(false),b2=new Wrapper<Boolean>(true);
								int w=getWidth()*4/5,h=getHeight()*4/5;
								JDialog conf=new JDialog(th,true);
								conf.setContentPane(new JPanel(){
									public void paintComponent(Graphics g){
										Graphics2D g2=(Graphics2D)g;
										g2.setPaint(new RadialGradientPaint(getWidth()*3/4,getHeight()*9/10,getHeight(),new float[]{0,0.1f,1},new Color[]{new Color(97,97,79),new Color(69,60,48),new Color(51,46,35)}));
										g2.fillRect(0,0,getWidth(),getHeight());
										Painter.lightInterBorder(g2,w,h,new Color(64,37,26,50),10);
									}
								});
								conf.setUndecorated(true);
								conf.setBounds(getWidth()/10,getHeight()/10,getWidth()*4/5,getHeight()*4/5);
								conf.setLayout(null);
								Switcher notToSend=new Switcher();
								JLabel notToSendText=new JLabel("Do not approve model (save only)");
								notToSendText.setFont(new Font(Font.DIALOG,Font.PLAIN,h/35));
								FontMetrics fm=notToSendText.getFontMetrics(notToSendText.getFont());
								JTextField price=new JTextField();
								if(User.getActiveUser().role==Role.SD_MANAGER){
									price.setBounds(w/2-w/10,h*2/3-h/60,w/5,h/30);
									conf.add(price);
								}
								else if(User.getActiveUser().role!=Role.TESTER){
									notToSend.setBounds(w/2+w/60,h*2/3-h/60,w/25,h/30);
									notToSendText.setBounds(w/2-(fm.stringWidth("Do not approve model (save only)")+w/30),h*2/3-h/60,fm.stringWidth("Do not approve model (save only)")+w/60,h/30);
									notToSendText.setForeground(new Color(104,105,110));
									conf.add(notToSend);conf.add(notToSendText);
								}
								HButton no=new HButton(){
									public void paint(Graphics g){
										Graphics2D g2=(Graphics2D)g;
										g2.setPaint(new RadialGradientPaint(getWidth()/3,getHeight()/3,getWidth(),new float[]{0,1},new Color[]{new Color(113,120,42),new Color(87,92,39)}));
										g2.fillRect(0,0,getWidth(),getHeight());
										g2.setPaint(new GradientPaint(getWidth()/2,getHeight()/3,new Color(0,0,0,50),getWidth()/2+getHeight()/30,getHeight()/2+getHeight()/150,new Color(0,0,0,0)));
										g2.fillRect(0,0,getWidth(),getHeight());
										g2.setFont(notToSendText.getFont());
										g2.setColor(Color.BLACK);
										g2.drawString("CANCEL",(getWidth()-fm.stringWidth("CANCEL"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
										if(pressed){
											g2.setColor(new Color(0,0,0,50));
											g2.fillRect(0,0,getWidth(),getHeight());
										}
										g2.setColor(Color.LIGHT_GRAY);
										g2.drawRect(0,0,getWidth()-1,getHeight()-1);
										g2.drawRect(scale+2,scale+2,getWidth()-(scale*2+5),getHeight()-(scale*2+4));
									}
								};
								no.setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent e){conf.dispose();}
								});
								HButton yes=new HButton(){
									public void paint(Graphics g){
										Graphics2D g2=(Graphics2D)g;
										g2.setPaint(new RadialGradientPaint(getWidth()/3,getHeight()/3,getWidth(),new float[]{0,1},new Color[]{new Color(113,120,42),new Color(87,92,39)}));
										g2.fillRect(0,0,getWidth(),getHeight());
										g2.setPaint(new GradientPaint(getWidth()/2,getHeight()/3,new Color(0,0,0,50),getWidth()/2+getHeight()/30,getHeight()/2+getHeight()/150,new Color(0,0,0,0)));
										g2.fillRect(0,0,getWidth(),getHeight());
										g2.setFont(notToSendText.getFont());
										g2.setColor(Color.BLACK);
										g2.drawString("CONFIRM",(getWidth()-fm.stringWidth("CONFIRM"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
										if(pressed){
											g2.setColor(new Color(0,0,0,50));
											g2.fillRect(0,0,getWidth(),getHeight());
										}
										g2.setColor(Color.LIGHT_GRAY);
										g2.drawRect(0,0,getWidth()-1,getHeight()-1);
										g2.drawRect(scale+2,scale+2,getWidth()-(scale*2+5),getHeight()-(scale*2+4));
									}
								};
								yes.setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent e){
										if(User.getActiveUser().role!=Role.SD_MANAGER||Pattern.matches("\\d+",price.getText())){
											b1.var=true;
											b2.var=!notToSend.on;
											conf.dispose();
										}
									}
								});
								no.setBounds(w/3-h/10,h-h/9,h/5,h/10);
								yes.setBounds(w*2/3-h/10,h-h/9,h/5,h/10);
								conf.add(yes);conf.add(no);
								conf.setVisible(true);
								if(b1.var&&(User.getActiveUser().role!=Role.SD_MANAGER||!price.getText().isBlank()))try{
									if(User.getActiveUser().role!=Role.TESTER){
										m.weight=Integer.parseInt(weight.getText());
										m.name=name.getText();
										m.article=article.getText();
									}
									if(b2.var){
										m.nextStage(User.getActiveUser());
										if(User.getActiveUser().role==Role.SD_MANAGER)m.price=Integer.parseInt(price.getText());
									}
									dispose();
									Saver.saveModels();
								}catch(NumberFormatException ex){submit.setEnabled(true);}
								else submit.setEnabled(true);
							}
						}.start();
					}
				});
				HButton reject=new HButton(){
					public void paint(Graphics g){
						Graphics2D g2=(Graphics2D)g;
						Polygon p=new Polygon(new int[]{0,getWidth()/2,getWidth(),getWidth(),getWidth()/2,0},new int[]{getHeight()/4,0,getHeight()/4,getHeight()*3/4,getHeight(),getHeight()*3/4},6);
						if(isEnabled())g2.setPaint(new RadialGradientPaint(getWidth()*3/5,getHeight()*2/3,getWidth(),new float[]{0,1},new Color[]{new Color(79,46,36),new Color(59,34,27)}));
						else g2.setColor(Color.DARK_GRAY);
						g2.fillPolygon(p);
						g2.setStroke(new BasicStroke(getHeight()/40));
						if(isEnabled())g2.setPaint(new RadialGradientPaint(getWidth()*3/5,getHeight()*2/3,getWidth(),new float[]{0,1},new Color[]{new Color(35,35,74),new Color(26,26,56)}));
						else g2.setColor(Color.BLACK);
						g2.drawPolygon(p);
						g2.setStroke(new BasicStroke(getHeight()/20));
						g2.setColor(Color.RED);
						int l=getHeight()/3;
						if(isEnabled()){
							g2.drawLine(getWidth()/2-l,getHeight()/2-l,getWidth()/2+l,getHeight()/2+l);
							g2.drawLine(getWidth()/2+l,getHeight()/2-l,getWidth()/2-l,getHeight()/2+l);
						}
					}
				};
				reject.setBounds(getWidth()*2/3-getHeight()/10,getHeight()-getHeight()/9,getHeight()/5,getHeight()/10);
				reject.setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						int w=getWidth()*4/5,h=getHeight()*4/5;
						Wrapper<Boolean>b1=new Wrapper<Boolean>(false);
						JDialog conf=new JDialog(th,true);
						conf.setContentPane(new JPanel(){
							public void paintComponent(Graphics g){
								Graphics2D g2=(Graphics2D)g;
								g2.setPaint(new RadialGradientPaint(getWidth()*3/4,getHeight()*9/10,getHeight(),new float[]{0,0.1f,1},new Color[]{new Color(97,97,79),new Color(69,60,48),new Color(51,46,35)}));
								g2.fillRect(0,0,getWidth(),getHeight());
								Painter.lightInterBorder(g2,w,h,new Color(64,37,26,50),10);
							}
						});
						conf.setUndecorated(true);
						conf.setBounds(getWidth()/10,getHeight()/10,getWidth()*4/5,getHeight()*4/5);
						conf.setLayout(null);
						conf.setFont(new Font(Font.DIALOG,Font.PLAIN,h/35));
						FontMetrics fm=conf.getFontMetrics(conf.getFont());
						JTextArea comment=new JTextArea(){
							public void paintComponent(Graphics g){
								Graphics2D g2=(Graphics2D)g;
								g2.setColor(new Color(98,105,89,100));
								g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
								g2.setFont(conf.getFont());
								String[]s=getText().split("\n");
								int height=fm.getAscent()+fm.getLeading()-fm.getDescent();
								g2.setColor(Color.BLACK);
								for(int i=0;i<s.length;i++)g2.drawString(s[i],(getWidth()-fm.stringWidth(s[i]))/2,(getHeight()+height)/2+fm.getHeight()*(i-s.length/2));
							}
						};
						comment.addKeyListener(new KeyAdapter(){
							public void keyTyped(KeyEvent e){comment.repaint();}
						});
						comment.setBounds(w/4,h/4,w/2,h/3);
						comment.setOpaque(false);
						HButton no=new HButton(){
							public void paint(Graphics g){
								Graphics2D g2=(Graphics2D)g;
								g2.setPaint(new RadialGradientPaint(getWidth()/3,getHeight()/3,getWidth(),new float[]{0,1},new Color[]{new Color(113,120,42),new Color(87,92,39)}));
								g2.fillRect(0,0,getWidth(),getHeight());
								g2.setPaint(new GradientPaint(getWidth()/2,getHeight()/3,new Color(0,0,0,50),getWidth()/2+getHeight()/30,getHeight()/2+getHeight()/150,new Color(0,0,0,0)));
								g2.fillRect(0,0,getWidth(),getHeight());
								g2.setFont(conf.getFont());
								g2.setColor(Color.BLACK);
								g2.drawString("CANCEL",(getWidth()-fm.stringWidth("CANCEL"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
								if(pressed){
									g2.setColor(new Color(0,0,0,50));
									g2.fillRect(0,0,getWidth(),getHeight());
								}
								g2.setColor(Color.LIGHT_GRAY);
								g2.drawRect(0,0,getWidth()-1,getHeight()-1);
								g2.drawRect(scale+2,scale+2,getWidth()-(scale*2+5),getHeight()-(scale*2+4));
							}
						};
						no.setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent e){conf.dispose();}
						});
						HButton yes=new HButton(){
							public void paint(Graphics g){
								Graphics2D g2=(Graphics2D)g;
								g2.setPaint(new RadialGradientPaint(getWidth()/3,getHeight()/3,getWidth(),new float[]{0,1},new Color[]{new Color(113,120,42),new Color(87,92,39)}));
								g2.fillRect(0,0,getWidth(),getHeight());
								g2.setPaint(new GradientPaint(getWidth()/2,getHeight()/3,new Color(0,0,0,50),getWidth()/2+getHeight()/30,getHeight()/2+getHeight()/150,new Color(0,0,0,0)));
								g2.fillRect(0,0,getWidth(),getHeight());
								g2.setFont(conf.getFont());
								g2.setColor(Color.BLACK);
								g2.drawString("CONFIRM",(getWidth()-fm.stringWidth("CONFIRM"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
								if(pressed){
									g2.setColor(new Color(0,0,0,50));
									g2.fillRect(0,0,getWidth(),getHeight());
								}
								g2.setColor(Color.LIGHT_GRAY);
								g2.drawRect(0,0,getWidth()-1,getHeight()-1);
								g2.drawRect(scale+2,scale+2,getWidth()-(scale*2+5),getHeight()-(scale*2+4));
							}
						};
						yes.setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent e){
								b1.var=true;
								conf.dispose();
							}
						});
						no.setBounds(w/3-h/10,h-h/9,h/5,h/10);
						yes.setBounds(w*2/3-h/10,h-h/9,h/5,h/10);
						conf.add(comment);
						conf.add(yes);conf.add(no);
						conf.setVisible(true);
						if(b1.var){
							m.comments.add(new Comment(">"+Editable.PROCESSES[m.stage]+":"+comment.getText()));
							m.stage=0;
							dispose();
							Saver.saveModels();
						}
					}
				});
				tab2.add(reject);
				if(User.getActiveUser().role==Role.SD_MANAGER)reject.setEnabled(false);
			}
		}else submit.setAction(cancel);
		JPanel history=new JPanel(null);
		history.setPreferredSize(new Dimension(getWidth(),getHeight()*m.comments.size()/4));
		history.setOpaque(false);
		JScrollPane sp=new JScrollPane(history,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setSize(getWidth(),getHeight());
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(getHeight()/30);
		class LocalComment extends JComponent{
			private static final float[]fr={0,0.4f,0.5f,0.6f,1};
			private static final Color[]c1={new Color(44,66,65),new Color(47,77,75),new Color(90,145,143),new Color(47,77,75),new Color(44,66,65)};
			private static final Color[]c2={new Color(68,71,71),new Color(87,94,94),new Color(110,125,125),new Color(87,94,94),new Color(68,71,71)};
			private final BasicStroke stroke=new BasicStroke(th.getHeight()/150);
			private Comment s;
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new LinearGradientPaint(0,0,getHeight(),getHeight(),fr,c2));
				g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
				g2.setPaint(new LinearGradientPaint(0,0,getHeight(),getHeight(),fr,c1));
				g2.setStroke(stroke);
				g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
				if(s.text.charAt(0)==':'){
					switch(s.text.split(":")[1]){
						case "COMPLETE":
							g2.setColor(new Color(126,173,81));
							g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/(4)));
							FontMetrics fm=g2.getFontMetrics();
							g2.drawString("PROCESS COMPLETE",(getWidth()-fm.stringWidth("PROCESS COMPLETE"))/2,fm.getAscent()+fm.getLeading()-fm.getDescent());
							g2.setColor(new Color(15,23,12));
							String t=s.text.substring(s.text.split(":")[1].length()+2,s.text.length());
							g2.setFont(new Font(Font.MONOSPACED,Font.BOLD,getHeight()/2));
							fm=g2.getFontMetrics();
							g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						break;
					}
				}else if(s.text.charAt(0)=='>'){
					g2.setColor(new Color(87,31,14));
					g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/(4)));
					g2.drawString(s.text.split(":")[0],getWidth()/100,getHeight()-getHeight()/100);
					g2.setColor(Color.BLACK);
					String[]t=s.text.substring(s.text.split(":")[0].length()+1,s.text.length()).split("\n");
					g2.setFont(new Font(Font.MONOSPACED,Font.BOLD,getHeight()/(t.length*2)));
					FontMetrics fm=g2.getFontMetrics();
					for(int i=0;i<t.length;i++)g2.drawString(t[i],(getWidth()-fm.stringWidth(t[i]))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2+fm.getHeight()*(i-t.length/2));
				}
				g2.setColor(new Color(126,127,133));
				g2.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/10));
				FontMetrics fm=g2.getFontMetrics();
				String time=s.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				g2.drawString(time,getWidth()-fm.stringWidth(time),getHeight());
			}
			public LocalComment(Comment s,int index){
				setBounds(th.getWidth()/10,index*th.getHeight()/4+th.getHeight()/40,th.getWidth()*4/5,th.getHeight()/5);
				this.s=s;
				addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.text),null);
					}
				});
			}
		}
		int i=0;
		if(m.comments.isEmpty()){
			JLabel l=new JLabel("Logs are empty.");
			l.setBounds(0,0,getWidth(),getHeight());
			l.setFont(new Font(Font.DIALOG,Font.BOLD,getHeight()/10));
			history.add(l);
		}else for(Comment c:m.comments){
			history.add(new LocalComment(c,i));
			i++;
		}
		tab3.add(sp);
		tab1.setBackground(new Color(102,107,89));
		tab2.setBackground(new Color(88,112,55));
		tab3.setBackground(new Color(41,38,13));
		add(tab1,"tab1");add(tab2,"tab2");add(tab3,"tab3");
		layout.show(getContentPane(),"tab2");
		setVisible(true);
	}
}