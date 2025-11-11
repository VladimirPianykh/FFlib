package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.bpa4j.Wrapper;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.PathIcon;

public class SwingCalendarRenderer<T extends Calendar.Event> implements FeatureRenderer<Calendar<T>>{
    private Calendar<T> contract;
    public SwingCalendarRenderer(Calendar<T> contract){
        this.contract=contract;
    }
    public Calendar<T> getTransmissionContract(){
        return contract;
    }
    public void render(FeatureRenderingContext ctx){
        SwingFeatureRenderingContext swingCtx=(SwingFeatureRenderingContext)ctx;
        JPanel content=swingCtx.getTarget();
        JPanel tab=new JPanel(null);
        tab.setSize(content.getSize());
        Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/30);
        fillTab(content,tab,font);
        content.add(tab);
        content.revalidate();
    }
    public void renderPreview(FeatureRenderingContext ctx){
        SwingPreviewRenderingContext previewCtx=(SwingPreviewRenderingContext)ctx;
        BufferedImage image=previewCtx.getPreviewTarget();
        int size=image.getHeight();
        Graphics2D g2=image.createGraphics();
        paint(g2,image,size);
        g2.dispose();
    }
    public void paint(Graphics2D g2,BufferedImage image,int h){
        g2.setStroke(new BasicStroke(h/20));
        g2.drawRoundRect(h/20,h/5,h*9/10,h*3/5,h/10,h/10);
        g2.setClip(new RoundRectangle2D.Double(h/20,h/5,h*9/10,h*3/5,h/10,h/10));
        g2.drawLine(0,h/3,h,h/3);
        g2.setStroke(new BasicStroke(h/30));
        for(int i=1;i<=6;++i){
            int x=h/20+i*h*9/70;
            g2.drawLine(x,h/3,x,h);
        }
        for(int i=1;i<=3;++i){
            int y=h/3+i*h*7/60;
            g2.drawLine(0,y,h,y);
        }
    }
    public void fillTab(JPanel content,JPanel tab,Font font){
        if(contract.getEventFiller()!=null){
            contract.clearEvents();
            contract.getEventFiller().accept(contract.getEvents());
        }
        JPanel panel=new JPanel(new GridLayout(0,7));
        Wrapper<YearMonth> w=new Wrapper<>(YearMonth.now());
        JPanel weekDays=new JPanel(new GridLayout(1,7));
        for(String s:new String[]{"Пн","Вт","Ср","Чт","Пт","Сб","Вс"}){
            JLabel l=new JLabel(s);
            l.setHorizontalAlignment(JLabel.CENTER);
            weekDays.add(l);
        }
        HButton left=new HButton(){
            private PathIcon icon=new PathIcon("ui/left.png");
            public void paint(Graphics g){
                int c=50-scale*4;
                if(getModel().isPressed()) c-=25;
                g.setColor(new Color(c,c,c));
                g.fillRect(0,0,getWidth(),getHeight());
                icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
            }
        },right=new HButton(){
            private PathIcon icon=new PathIcon("ui/right.png");
            public void paint(Graphics g){
                int c=50-scale*4;
                if(getModel().isPressed()) c-=25;
                g.setColor(new Color(c,c,c));
                g.fillRect(0,0,getWidth(),getHeight());
                icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
            }
        };
        left.addActionListener(e->{
            w.var=w.var.minus(1,ChronoUnit.MONTHS);
            fillForMonth(panel,w.var);
            panel.revalidate();
        });
        right.addActionListener(e->{
            w.var=w.var.plus(1,ChronoUnit.MONTHS);
            fillForMonth(panel,w.var);
            panel.revalidate();
        });
        weekDays.setBounds(tab.getWidth()/10,0,tab.getWidth()*4/5,tab.getHeight()/10);
        left.setSize(tab.getWidth()/10,tab.getHeight());
        panel.setBounds(tab.getWidth()/10,tab.getHeight()/10,tab.getWidth()*4/5,tab.getHeight()*9/10);
        right.setBounds(tab.getWidth()*9/10,0,tab.getWidth()/10,tab.getHeight());
        tab.add(weekDays);
        tab.add(left);
        tab.add(panel);
        tab.add(right);
        fillForMonth(panel,w.var);
    }
    private void fillForMonth(JPanel panel,YearMonth m){
        panel.removeAll();
        for(int i=1;i<m.atDay(1).getDayOfWeek().getValue();++i)
            panel.add(new JComponent(){
                public void paint(Graphics g){
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0,0,getWidth(),getHeight());
                }
            });
        for(int i=1;i<=m.lengthOfMonth();++i){
            LocalDate d=m.atDay(i);
            panel.add(contract.getDater().apply(contract.getEventList(d),d));
        }
        for(int i=0;i<7-m.atEndOfMonth().getDayOfWeek().getValue();++i)
            panel.add(new JComponent(){
                public void paint(Graphics g){
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0,0,getWidth(),getHeight());
                }
            });
    }
}
