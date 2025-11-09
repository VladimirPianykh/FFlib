package com.bpa4j.ui.swing.editor.modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import com.bpa4j.core.Editable;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.ui.swing.SwingModularEditorRenderer.SwingModuleRenderingContext;
import com.bpa4j.ui.swing.util.HButton;

public class SwingStageApprovalModuleRenderer extends SwingFormModuleRenderer{
    protected JPanel createTabPanel(Editable editable,boolean isNew,Runnable deleter,FormModule module,ModulesRenderingContext context){
        SwingModuleRenderingContext ctx=(SwingModuleRenderingContext)context;
        Processable p=(Processable)editable;
        JPanel tab=super.createTabPanel(editable,isNew,deleter,module,context);
        JButton ok=(JButton)tab.getComponent(1);
        ActionListener a=ok.getActionListeners()[0];
        ok.removeActionListener(a);
        if(User.getActiveUser().hasPermission(p.getStage().approver)) ok.addActionListener(evt->{
            boolean notReady=ok.getText().equals("Далее");
            a.actionPerformed(evt);
            if(p.isLastStage()||notReady) return;
            JDialog d=new JDialog(ctx.getEditorDialog(),true);
            d.setBounds(Root.SCREEN_SIZE.width/10,Root.SCREEN_SIZE.height/10,Root.SCREEN_SIZE.width*4/5,Root.SCREEN_SIZE.height*4/5);
            d.setUndecorated(true);
            d.setContentPane(new JPanel(null){
                public void paintComponent(Graphics g){
                    Graphics2D g2=(Graphics2D)g;
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(0,0,getWidth(),getHeight());
                    g2.setStroke(new BasicStroke(getHeight()/50));
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawRect(0,0,getWidth(),getHeight());
                }
            });
            JLabel l=new JLabel(p.getStage().name);
            l.setBounds(d.getWidth()/10,d.getHeight()/30,d.getWidth()*4/5,d.getHeight()/15);
            l.setHorizontalAlignment(JLabel.CENTER);
            l.setForeground(Color.WHITE);
            l.setFont(new Font(Font.DIALOG,Font.PLAIN,d.getHeight()/20));
            d.add(l);
            JTextArea comment=new JTextArea();
            comment.setBorder(BorderFactory.createTitledBorder(null,"Комментарий",TitledBorder.BOTTOM,0,new Font(Font.DIALOG,Font.PLAIN,d.getHeight()/40),Color.BLACK));
            comment.setBounds(d.getWidth()/4,d.getHeight()/6,d.getWidth()/2,d.getHeight()/2);
            d.add(comment);
            class B extends HButton{
                public B(){
                    super(30,5);
                }
                public void paint(Graphics g){
                    Graphics2D g2=(Graphics2D)g;
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/5,getHeight()/5);
                    g2.setStroke(new BasicStroke(getHeight()/20));
                    g2.setColor(getForeground());
                    g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/5,getHeight()/5);
                    AffineTransform a=g2.getTransform(),initA=g2.getTransform();
                    int r=Math.max(0,(21-scale)*5);
                    a.rotate(Math.toRadians(r),getWidth()/2,getHeight()/2);
                    g2.setTransform(a);
                    g2.setColor(new Color(54,56,49));
                    int d=Math.min(getWidth(),getHeight()+Math.max(0,(getWidth()-getHeight())*(scale-9)))*2/3;
                    g2.fillRoundRect((getWidth()-d)/2,getHeight()/6,d,getHeight()*2/3,getHeight()/5,getHeight()/5);
                    g2.setTransform(initA);
                    FontMetrics fm=g2.getFontMetrics();
                    g2.setColor(Color.WHITE);
                    String s=getText().substring(0,(getText().length()-1)*Math.max(0,scale-9)/(maxScale-9)+1);
                    g2.drawString(s,(getWidth()-fm.stringWidth(s))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
                    if(getModel().isPressed()){
                        g2.setColor(new Color(0,0,0,50));
                        g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/5,getHeight()/5);
                    }
                }
            }
            B approve=new B();
            approve.addActionListener(e->{
                p.approve(comment.getText());
                d.dispose();
            });
            approve.setBounds(User.getActiveUser().hasPermission(p.getStage().rejecter)?d.getWidth()*3/5:d.getWidth()*2/5,d.getHeight()*9/10,d.getWidth()/5,d.getHeight()/15);
            approve.setText("Одобрить");
            approve.setBackground(new Color(90,125,10));
            approve.setForeground(new Color(33,43,9));
            d.add(approve);
            if(User.getActiveUser().hasPermission(p.getStage().rejecter)){
                B reject=new B(){};
                reject.addActionListener(e->{
                    p.reject(comment.getText());
                    d.dispose();
                });
                reject.setBounds(d.getWidth()/5,d.getHeight()*9/10,d.getWidth()/5,d.getHeight()/15);
                reject.setText("Отклонить");
                reject.setBackground(new Color(125,56,10));
                reject.setForeground(new Color(43,13,8));
                d.add(reject);
            }
            d.setVisible(true);
        });
        else ok.setVisible(false);
        return tab;
    }
}
