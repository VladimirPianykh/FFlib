package com.futurefactory;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
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
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.futurefactory.Data.Editable;
import com.futurefactory.User.Feature;
import com.futurefactory.User.Permission;
import com.futurefactory.User.Role;

public class WorkFrame extends JFrame{
	public static class WorkTabButton extends HButton{
		Feature feature;
		BufferedImage image;
		public WorkTabButton(Feature feature,JPanel content,int index){
			this.feature=feature;
			int h=content.getHeight()/3;
			image=new BufferedImage(h,h,6);
			Graphics2D g2=image.createGraphics();
			g2.setPaint(new GradientPaint(0,0,new Color(13,16,31,100),h,h,new Color(11,18,31,100)));
			switch(feature){
				case HISTORY->{
					Area p=new Area(new Arc2D.Double(h/10,h/10,h*4/5,h*4/5,-90,270,Arc2D.PIE));
					p.subtract(new Area(new Ellipse2D.Double(h/5,h/5,h*3/5,h*3/5)));
					p.add(new Area(new Polygon(new int[]{h/10-h/30,h/5+h/30,h*3/20},new int[]{h/2,h/2,h/2+h/10},3)));
					g2.fillPolygon(new int[]{h/2-h/100,h/2+h/100,h/2+h/30,h*3/4,h*3/4,h/2-h/30},new int[]{h/4,h/4,h/2-h/30,h*2/3-h/50,h*2/3,h/2+h/30},6);
					g2.fill(p);
				}
				case ROLE_SETTING->{
					g2.setStroke(new BasicStroke(h/15,2,0));
					g2.drawOval(h/3,h/3-h/6,h/3,h/3);
					g2.drawPolyline(new int[]{h/4,h/2-h/10,h/2+h/10,h*3/4},new int[]{h,h/2+h/15,h/2+h/15,h},4);
				}
				case MODEL_EDITING->{
					//g2.drawPolygon(new int[]{h/4,h/2-h/10,h/2+h/10,h*3/4},new int[]{h,h/2+h/15,h/2+h/15,h},4);
					g2.setStroke(new BasicStroke(h/40));
					g2.drawPolyline(new int[]{h/4,h/4+h/20,h*3/4+h/20,h*3/4+h/20+h/10,h*3/4+h/5,h*3/4+h/5,h*3/4+h/5-h/40,h*3/4+h/5-h/40,h*3/4+h/20+h/10,h*3/4+h/40},new int[]{h*3/4,h*3/4,h/4,h/4,h/4-h/20,h/4-h/20-h/10,h/4-h/20-h/10,h/4-h/10,h/4-h/20,h/4-h/20},10);
					for(int x=0;x<h;x++)for(int y=0;y<h-x;y++)
					image.setRGB(x,y,image.getRGB(h-y-1,h-x-1));
				}
				/*case REPORT->{
					g2.setStroke(new BasicStroke(h/15,2,0));
					g2.drawPolygon(new int[]{h/3,h*2/3-h/20,h*2/3,h*2/3,h/3},new int[]{h/5,h/5,h/5+h/20,h*4/5,h*4/5},5);
					for(int i=0;i<4;i++)g2.drawLine(h*4/9,h*(3+i)/10,h*5/9,h*(3+i)/10);
				}*/
			}
			g2.dispose();
			JPanel tab=new JPanel(null);
			Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/20);
			switch(feature){
				case HISTORY->setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						if(tab.getComponentCount()>0){
							((CardLayout)content.getLayout()).show(content,"tab"+index);
							content.revalidate();
							return;
						}
						JPanel p=createTable(User.getActiveUser().history.size()+1,4,tab);
						JLabel[]data0={new JLabel("login"),new JLabel("login time"),new JLabel("logout time"),new JLabel("IP")};
						for(JLabel l:data0){
							l.setFont(font);
							l.setForeground(Color.BLACK);
							p.add(l);
						}
						User.getActiveUser().history.descendingIterator().forEachRemaining(a->{
							JLabel[]data={new JLabel(a.login),new JLabel(a.inTime.toString()),new JLabel(a.outTime==null?"---":a.outTime.toString()),new JLabel(a.ip.toString())};
							for(JLabel l:data){
								l.setFont(font);
								l.setToolTipText(l.getText());
								p.add(l);
							}
						});
						((CardLayout)content.getLayout()).show(content,"tab"+index);
						content.revalidate();
					}
				});
				case ROLE_SETTING->setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						if(tab.getComponentCount()>0){
							((CardLayout)content.getLayout()).show(content,"tab"+index);
							content.revalidate();
							return;
						}
						JPanel p=createTable(User.getUserCount(),5,tab);
						Role[]roles=Role.values();
						User.forEachUser(u->{
							JLabel name=new JLabel(u.login);
							name.setFont(font);
							JComboBox<Role>b=new JComboBox<Role>(roles);
							b.setSelectedItem(u.role);
							name.addMouseListener(new MouseAdapter(){
								public void mouseClicked(MouseEvent e){
									if(SwingUtilities.isRightMouseButton(e)){
										User.deleteUser(u.login);
										name.setText("<deleted>");
										name.setEnabled(false);
										b.removeAllItems();
										User.save();
									}
								}
							});
							b.setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent e){
									u.role=(Role)b.getSelectedItem();
									User.save();
								}
							});
							p.add(name);
							p.add(b);
						});
						((CardLayout)content.getLayout()).show(content,"tab"+index);
						content.revalidate();
					}
				});
				case MODEL_EDITING->setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						Data d=Data.getInstance();
						if(tab.getComponentCount()>0){
							((CardLayout)content.getLayout()).show(content,"tab"+index);
							content.revalidate();
							return;
						}
						JPanel p=createTable(d.editables.size()+(User.getActiveUser().hasPermission(Permission.CREATE)?1:0),1,tab);
						int s=content.getHeight()/7;
						BufferedImage editImage=new BufferedImage(content.getHeight()/7,content.getHeight()/7,6),
						addImage=new BufferedImage(content.getHeight()/7,content.getHeight()/7,6);
						Graphics2D g2=addImage.createGraphics();
						g2.setPaint(new RadialGradientPaint(s/2,s/2,s/2,new float[]{0,1},new Color[]{new Color(94,86,82),new Color(66,60,57)}));
						g2.fillRect(0,0,s,s);
						g2.setStroke(new BasicStroke(s/40,2,2));
						g2.setPaint(new RadialGradientPaint(s/2,s/2,s/2,new float[]{0,1},new Color[]{new Color(110,128,40),new Color(81,92,36)}));
						g2.drawRect(s/3,s/10,s/3,s*4/5);
						g2.drawRect(s/10,s/3,s*4/5,s/3);
						Color c1=new Color(42,46,30),c2=new Color(32,36,21);
						for(Editable r:Data.getInstance().editables){
							HButton b=new HButton(){
								public void paintComponent(Graphics g){
									Graphics2D g2=(Graphics2D)g;
									g2.setColor(Color.GRAY);
									g2.fillRect(0,0,getWidth(),getHeight());
									g2.setFont(font);
									FontMetrics fm=g2.getFontMetrics();
									g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
									g2.drawString(r.name,getWidth()/100,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
									g2.drawImage(editImage,getWidth()-editImage.getWidth(),0,this);
									g2.setColor(new Color(0,0,0,scale*10));
									g2.fillRect(0,0,getWidth(),getHeight());
									if(true){//FILLIN
										g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(89+scale*5,87,63),new Color(122+scale*5,119,80)},CycleMethod.REFLECT));
										g2.fillOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
										g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(135+scale*5,52,14),new Color(194+scale*5,47,17)},CycleMethod.REFLECT));
										g2.fillOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
										g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(54,51,36),new Color(77,74,56)},CycleMethod.REFLECT));
										g2.drawOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
										g2.drawOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
									}
								}
							};
							b.setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent e){
									if(ProgramStarter.editor==null)throw new RuntimeException("Editor ha not been set.");
									ProgramStarter.editor.constructEditor(r);
								}
							});
							p.add(b);
						};
						if(User.getActiveUser().hasPermission(Permission.CREATE)){
							HButton add=new HButton(){
							public void paintComponent(Graphics g){
								g.setColor(Color.GRAY);
								g.fillRect(0,0,getWidth(),getHeight());
								g.drawImage(addImage,(getWidth()-addImage.getWidth())/2,0,this);
								g.setColor(new Color(0,0,0,scale*10));
								g.fillRect(0,0,getWidth(),getHeight());
							}};
							add.setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent e){
									if(ProgramStarter.editor==null)throw new RuntimeException("Editor ha not been set.");
									ProgramStarter.editor.constructEditor(null);
									SwingUtilities.getWindowAncestor(content).dispose();
									ProgramStarter.frame=new WorkFrame(User.getActiveUser());
								}
							});
							p.add(add);
						}
						((CardLayout)content.getLayout()).show(content,"tab"+index);
						content.revalidate();
					}
				});
				/*case REPORT->setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						if(tab.getComponentCount()>0){
							((CardLayout)content.getLayout()).show(content,"tab"+index);
							content.revalidate();
							return;
						}
						HButton b=new HButton(){
							public void paint(Graphics g){
								Graphics2D g2=(Graphics2D)g;
								g2.setPaint(new LinearGradientPaint(getWidth()/2,0,getWidth(),getHeight(),new float[]{0.4f,0.41f,0.5f,0.51f},new Color[]{Color.GRAY,Color.LIGHT_GRAY,Color.LIGHT_GRAY,Color.GRAY}));
								g2.fillRect(0,0,getWidth(),getHeight());
								g2.setStroke(new BasicStroke(scale));
								g2.setColor(Color.DARK_GRAY);
								g2.drawRect(0,0,getWidth(),getHeight());
							}
						};
						b.setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent e){try{
							File file=new File("C:/Users/user/Downloads/report.xls");
							if(file.exists())file.delete();
							FileOutputStream fOS=new FileOutputStream("C:/Users/user/Downloads/report.xls");
							HSSFWorkbook w=new HSSFWorkbook();
							Sheet sheet=w.createSheet("price-list");
							Collection<Model>models=Model.modelMap.values();
							Row r0=sheet.createRow(0);
							r0.createCell(0).setCellValue("№");
							r0.createCell(1).setCellValue("Товар");
							r0.createCell(2).setCellValue("Цена");
							int i=1;
							for(Model model:models){
								if(model.status==Status.PRODUCED){
									Row r=sheet.createRow(i);
									r.createCell(0).setCellValue(i);
									r.createCell(1).setCellValue(model.name);
									r.createCell(2).setCellValue(model.price+" RUB");
									i++;
								}
							}
							w.write(fOS);
							System.exit(0);
						}catch(IOException ex){ex.printStackTrace();}}});
						b.setBounds(content.getWidth()/4,content.getHeight()/2-content.getHeight()/6,content.getWidth()/2,content.getHeight()/3);
						tab.add(b);
						((CardLayout)content.getLayout()).show(content,"tab"+index);
						content.revalidate();
					}
				});*/
			}
			content.add(tab,"tab"+index);
		}
		public void paintComponent(Graphics g){
			Graphics2D g2=(Graphics2D)g;
			g2.setPaint(new LinearGradientPaint(0,0,getWidth()/2,getHeight()/2,new float[]{0,1},new Color[]{new Color(50-scale,50-scale,50-scale),Color.GRAY},CycleMethod.REFLECT));
			g2.fillRect(0,0,getWidth(),getHeight());
			g2.drawImage(image,0,0,this);
		}
		private static JPanel createTable(int rows,int cols,JPanel tab){
			JPanel p=new JPanel(new GridLayout(rows,2));
			p.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*rows/7));
			p.setSize(p.getPreferredSize());
			JScrollPane s=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			s.setSize(tab.getWidth(),Math.min(p.getHeight(),tab.getHeight()));
			s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
			tab.add(s);
			return p;
		}
	}
	public static HashMap<Role,Feature[]>ftrMap=new HashMap<Role,Feature[]>();
	static{
		ftrMap.put(Role.ADMIN,Feature.values());
		// ftrMap.put(Role.ENGINEER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
		// ftrMap.put(Role.PD_MANAGER,new Feature[]{Feature.HISTORY});
		// ftrMap.put(Role.PROCUREMENT_MANAGER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
		// ftrMap.put(Role.PRODUCTION_MANAGER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
		// ftrMap.put(Role.SALES_MANAGER,new Feature[]{Feature.HISTORY});
		// ftrMap.put(Role.SD_MANAGER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
		// ftrMap.put(Role.STOREKEEPER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
		// ftrMap.put(Role.TESTER,new Feature[]{Feature.HISTORY,Feature.MODEL_EDITING});
	}
	public WorkFrame(User user){
		setSize(Root.SCREEN_SIZE);
		setExtendedState(3);
		setUndecorated(true);
		setContentPane(new JPanel(null){
			float[]fr={0,1};
			Color[]c1={Color.GRAY,Color.DARK_GRAY};
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new RadialGradientPaint(getWidth()/2,0,getHeight(),fr,c1));
				g2.fillRect(0,0,getWidth(),getHeight());
			}
		});
		CardLayout l=new CardLayout();
		JPanel content=new JPanel(l);
		content.setOpaque(false);
		content.setBounds(0,getHeight()/4,getWidth(),getHeight()*3/4);
		add(content);
		JPanel sidebar=new JPanel(null){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new GradientPaint(0,0,new Color(63,82,45),getHeight()/10,getHeight(),new Color(47,61,34)));
				g2.fillRect(0,0,getWidth(),getHeight());
			}
		};
		sidebar.setOpaque(false); 
		JScrollPane s=new JScrollPane(sidebar,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		s.setOpaque(false);
		s.setBounds(0,0,getWidth(),getHeight()/4);
		HButton exit=new HButton(){
			private static final Color[]C1={new Color(112,30,4),new Color(115,55,3)},C2={new Color(135,123,35),new Color(110,100,32)};
			private static final float[]FR={0,1};
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10));
				g2.setPaint(new RadialGradientPaint(getWidth(),getHeight(),getHeight(),FR,C1));
				g2.fillRect(0,0,getWidth(),getHeight());
				Painter.lightInterBorder(g2,getWidth(),getHeight(),getForeground(),10);
				g2.setStroke(new BasicStroke(scale==0?0:getHeight()/40));
				g2.setPaint(new RadialGradientPaint(getWidth(),getHeight(),getHeight(),FR,C2));
				g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
			}
		};
		exit.setForeground(new Color(54,23,13));
		exit.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		exit.setBounds(0,0,getHeight()/4,getHeight()/4);
		add(exit);
		for(Feature f:ftrMap.get(user.role)){
			WorkTabButton t=new WorkTabButton(f,content,sidebar.getComponentCount());
			sidebar.add(t);
			t.setBounds(sidebar.getComponentCount()*getHeight()/4,0,getHeight()/4,getHeight()/4);
		}
		add(s);
		setVisible(true);
	}
}