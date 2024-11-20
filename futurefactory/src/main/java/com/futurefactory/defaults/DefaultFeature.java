package com.futurefactory.defaults;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.futurefactory.Data;
import com.futurefactory.ProgramStarter;
import com.futurefactory.User;
import com.futurefactory.WorkFrame;
import com.futurefactory.Data.Editable;
import com.futurefactory.Data.EditableGroup;
import com.futurefactory.User.Feature;
import com.futurefactory.User.Role;
import com.futurefactory.WorkFrame.WorkTabButton;
import java.util.NoSuchElementException;

public enum DefaultFeature implements Feature{
	HISTORY("История входов"){
		public void paint(Graphics2D g2,BufferedImage image,int h){
			Area p=new Area(new Arc2D.Double(h/10,h/10,h*4/5,h*4/5,-90,270,Arc2D.PIE));
			p.subtract(new Area(new Ellipse2D.Double(h/5,h/5,h*3/5,h*3/5)));
			p.add(new Area(new Polygon(new int[]{h/10-h/30,h/5+h/30,h*3/20},new int[]{h/2,h/2,h/2+h/10},3)));
			g2.fillPolygon(new int[]{h/2-h/100,h/2+h/100,h/2+h/30,h*3/4,h*3/4,h/2-h/30},new int[]{h/4,h/4,h/2-h/30,h*2/3-h/50,h*2/3,h/2+h/30},6);
			g2.fill(p);
		}
		public void fillTab(JPanel content,JPanel tab,Font font){
			JPanel p=WorkTabButton.createTable(User.getActiveUser().history.size()+1,4,tab,true);
			JLabel[]data0={new JLabel("логин"),new JLabel("время входа"),new JLabel("время выхода"),new JLabel("IP")};
			for(JLabel l:data0){
				l.setFont(font);
				l.setForeground(Color.WHITE);
				p.add(l);
			}
			User.getActiveUser().history.descendingIterator().forEachRemaining(a->{
				JLabel[]data={new JLabel(a.login),new JLabel(a.inTime.toString()),new JLabel(a.outTime==null?"---":a.outTime.toString()),new JLabel(a.ip.toString())};
				for(JLabel l:data){
					l.setFont(font);
					l.setForeground(Color.LIGHT_GRAY);
					l.setToolTipText(l.getText());
					p.add(l);
				}
			});
		}
	},
	ROLE_SETTING("Управление ролями"){
		public void paint(Graphics2D g2,BufferedImage image,int h){
			g2.setStroke(new BasicStroke(h/15,2,0));
			g2.drawOval(h/3,h/3-h/6,h/3,h/3);
			g2.drawPolyline(new int[]{h/4,h/2-h/10,h/2+h/10,h*3/4},new int[]{h,h/2+h/15,h/2+h/15,h},4);
		}
		public void fillTab(JPanel content,JPanel tab,Font font){
			JPanel p=WorkTabButton.createTable(User.getUserCount(),5,tab,true);
			Role[]roles=User.registeredRoles.toArray(new Role[0]);
			User.forEachUser(u->{
				JLabel name=new JLabel(u.login);
				name.setFont(font);
				name.setForeground(Color.WHITE);
				JComboBox<Role>b=new JComboBox<Role>(roles);
				b.setBackground(Color.DARK_GRAY);
				b.setForeground(Color.LIGHT_GRAY);
				b.setBorder(null);
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
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						u.role=(Role)b.getSelectedItem();
						User.save();
					}
				});
				p.add(name);
				p.add(b);
			});
		}
	},
	MODEL_EDITING("Редактирование данных"){
		public void paint(Graphics2D g2,BufferedImage image,int h){
			g2.setStroke(new BasicStroke(h/40));
			g2.drawPolyline(new int[]{h/4,h/4+h/20,h*3/4+h/20,h*3/4+h/20+h/10,h*3/4+h/5,h*3/4+h/5,h*3/4+h/5-h/40,h*3/4+h/5-h/40,h*3/4+h/20+h/10,h*3/4+h/40},new int[]{h*3/4,h*3/4,h/4,h/4,h/4-h/20,h/4-h/20-h/10,h/4-h/20-h/10,h/4-h/10,h/4-h/20,h/4-h/20},10);
			for(int x=0;x<h;x++)for(int y=0;y<h-x;y++)
			image.setRGB(x,y,image.getRGB(h-y-1,h-x-1));
		}
		public void fillTab(JPanel content,JPanel tab,Font font){
			Data d=Data.getInstance();
			tab.setLayout(new GridLayout(1,Data.getInstance().editables.size()));
			for(EditableGroup<?>group:d.editables){
				try{
					boolean canSee=User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("READ_"+group.type.getSimpleName().toUpperCase())).findAny().get()),
					canCreate=User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("CREATE_"+group.type.getSimpleName().toUpperCase())).findAny().get());
					JPanel subTab=new JPanel(null);
					subTab.setOpaque(false);
					subTab.setSize(tab.getWidth()/Data.getInstance().editables.size(),tab.getHeight());
					JPanel p=WorkTabButton.createTable(group.size()+(canCreate?1:0),1,subTab,false);
					if(canSee)for(Editable r:group){
						JButton b=group.createElementButton(r,font);
						b.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								if(ProgramStarter.editor==null)throw new RuntimeException("Editor has not been set.");
								ProgramStarter.editor.constructEditor(r);
							}
						});
						p.add(b);
					}
					if(canCreate){
						JButton add=group.createAddButton(font);
						add.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								try{
									if(ProgramStarter.editor==null)throw new RuntimeException("Editor has not been set.");
									Editable nEditable=null;
									nEditable=(Editable)group.type.getDeclaredConstructor().newInstance();
									group.add(nEditable);
									ProgramStarter.editor.constructEditor(nEditable);
									SwingUtilities.getWindowAncestor(content).dispose();
									ProgramStarter.frame=new WorkFrame(User.getActiveUser());
								}catch(Exception ex){throw new RuntimeException("Editable implementations must be passed as a `type` argument and have a default constructor.",ex);}
							}
						});
						p.add(add);
					}
					tab.add(subTab);
				}catch(NoSuchElementException ex){throw new RuntimeException("Permission for "+group.type+" not found. You must define READ_"+group.type.getSimpleName().toUpperCase()+" and CREATE_"+group.type.getSimpleName().toUpperCase()+" permissions.",ex);}
			}
			tab.revalidate();
			tab.repaint(); 
		}
	};
	/*REPORT{
		public void paint(Graphics2D g2,BufferedImage image,int h){
			g2.setStroke(new BasicStroke(h/15,2,0));
			g2.drawPolygon(new int[]{h/3,h*2/3-h/20,h*2/3,h*2/3,h/3},new int[]{h/5,h/5,h/5+h/20,h*4/5,h*4/5},5);
			for(int i=0;i<4;i++)g2.drawLine(h*4/9,h*(3+i)/10,h*5/9,h*(3+i)/10);
		}
		public void fillTab(JPanel content,JPanel tab,Font font){
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
			b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){try{
				File file=new File("C:/Users/user/Downloads/report.xls");
				if(file.exists())file.delete();
				FileOutputStream fOS=new FileOutputStream("C:/Users/user/Downloads/report.xls");
				HSSFWorkbook w=new HSSFWorkbook();
				Sheet sheet=w.createSheet("price-list");
				Collection<Editable>models=Data.getInstance().editables;
				Row r0=sheet.createRow(0);
				r0.createCell(0).setCellValue("№");
				r0.createCell(1).setCellValue("Товар");
				r0.createCell(2).setCellValue("Цена");
				int i=1;
				for(Editable model:models){
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
		}
	}*/
	private final String translation;
	private DefaultFeature(String translation){this.translation=translation;}
	public String toString(){return translation;}
}