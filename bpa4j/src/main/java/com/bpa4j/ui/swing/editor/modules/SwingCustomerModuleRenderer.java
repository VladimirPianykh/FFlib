package com.bpa4j.ui.swing.editor.modules;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import com.bpa4j.core.Editable;
import com.bpa4j.core.Editable.ActionRecord;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.CustomerModule;
import com.bpa4j.ui.swing.SwingModularEditorRenderer.SwingModuleRenderingContext;
import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.PathIcon;
import java.awt.BasicStroke;
import java.awt.Color;

public class SwingCustomerModuleRenderer implements ModuleRenderer<CustomerModule>{
    public void createTab(Editable editable,boolean isNew,Runnable deleter,CustomerModule module,ModulesRenderingContext context){
        SwingModuleRenderingContext ctx=(SwingModuleRenderingContext)context;
        JDialog editor=ctx.getEditorDialog();
        JPanel tab=new JPanel(null);
        tab.setBackground(Color.BLACK);
        JButton ok=new JButton(){
            public void paint(Graphics g){
                g.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight(),getHeight()));
                g.setColor(getModel().isPressed()?Color.DARK_GRAY:getBackground());
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
        ok.setBounds(editor.getWidth()/5,editor.getHeight()*7/10,editor.getWidth()/5,editor.getHeight()/20);
        ok.setText("Готово");
        ok.setOpaque(false);
        ok.setFont(new Font(Font.DIALOG,Font.PLAIN,ok.getHeight()));
        tab.add(ok);
        if(deleter!=null){
            HButton delete=new HButton(10,5){
                public void paint(Graphics g){
                    g.setColor(new Color(255-scale*8,20,20));
                    g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
                    ((Graphics2D)g).setStroke(new BasicStroke(getHeight()/20));
                    g.setColor(Color.DARK_GRAY);
                    g.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
                    g.setColor(Color.BLACK);
                    g.drawLine(getWidth()/3,getHeight()/3,getWidth()*2/3,getHeight()*2/3);
                    g.drawLine(getWidth()*2/3,getHeight()/3,getWidth()/3,getHeight()*2/3);
                    if(getModel().isPressed()){
                        g.setColor(new Color(0,0,0,100));
                        g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
                    }
                }
            };
            delete.addActionListener(e->{deleter.run();editor.dispose();});
            delete.setBounds(editor.getWidth()/10,editor.getHeight()*7/10,editor.getHeight()/20,editor.getHeight()/20);
            delete.setOpaque(false);
            tab.add(delete);
        }
        JLabel portrait=new JLabel(new PathIcon("ui/customer.png"));
        portrait.setBounds(editor.getWidth()*3/5,editor.getHeight()/6,editor.getHeight()/3,editor.getHeight()/3);
        portrait.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,new Color(200,200,230),new Color(50,50,60)));
        tab.add(portrait);
        DefaultListModel<String>m=new DefaultListModel<>();
        for(ActionRecord r:editable.records)if(r.text.startsWith(">PAY:"))m.addElement("Paid "+r.text.substring(5)+" on "+r.time.toLocalDate()+" at "+r.time.toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString());
        TitledBorder border=BorderFactory.createTitledBorder("История покупок");
        if(m.isEmpty()){
            JLabel emptyHistory=new JLabel("Сейчас здесь пусто :(");
            emptyHistory.setBounds(editor.getWidth()/10,editor.getHeight()/6,editor.getWidth()*2/5,editor.getHeight()/3);
            emptyHistory.setForeground(Color.LIGHT_GRAY);
            emptyHistory.setBorder(border);
            tab.add(emptyHistory);
        }else{
            JList<String>purchasesHistory=new JList<>(m);
            purchasesHistory.setBounds(editor.getWidth()/10,editor.getHeight()/6,editor.getWidth()*2/5,editor.getHeight()/3);
            purchasesHistory.setBorder(border);
            tab.add(purchasesHistory);
        }
        JTextField nameField=new JTextField(editable.name);
        nameField.setBounds(editor.getWidth()/2,editor.getHeight()*7/10,editor.getWidth()/10+editor.getHeight()/3,editor.getHeight()/20);
        tab.add(nameField);
        ok.addActionListener(e->{
            if(!nameField.getText().isBlank()){
                editable.name=nameField.getText();
                editor.dispose();
            }
        });
        ctx.getTabs().add(tab);
    }
}
