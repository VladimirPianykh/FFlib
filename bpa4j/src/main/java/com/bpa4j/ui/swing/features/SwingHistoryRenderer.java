package com.bpa4j.ui.swing.features;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.bpa4j.core.User;
import com.bpa4j.defaults.features.transmission_contracts.History;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.WorkTabButton;

public class SwingHistoryRenderer implements FeatureRenderer<History>{
    private History contract;
    public SwingHistoryRenderer(History contract){
        this.contract=contract;
    }
    public History getTransmissionContract(){
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
        Area p=new Area(new Arc2D.Double(h/10,h/10,h*4/5,h*4/5,-90,270,Arc2D.PIE));
        p.subtract(new Area(new Ellipse2D.Double(h/5,h/5,h*3/5,h*3/5)));
        p.add(new Area(new Polygon(new int[]{h/10-h/30,h/5+h/30,h*3/20},new int[]{h/2,h/2,h/2+h/10},3)));
        g2.fillPolygon(new int[]{h/2-h/100,h/2+h/100,h/2+h/30,h*3/4,h*3/4,h/2-h/30},new int[]{h/4,h/4,h/2-h/30,h*2/3-h/50,h*2/3,h/2+h/30},6);
        g2.fill(p);
    }
    public void fillTab(JPanel content,JPanel tab,Font font){
        JPanel p=WorkTabButton.createTable(User.getActiveUser().history.size()+1,4,tab,true);
        JLabel[] data0={new JLabel("логин"),new JLabel("время входа"),new JLabel("время выхода"),new JLabel("IP")};
        for(JLabel l:data0){
            l.setFont(font);
            l.setForeground(Color.WHITE);
            p.add(l);
        }
        User.getActiveUser().history.descendingIterator().forEachRemaining(a->{
            JLabel[] data={new JLabel(a.login),new JLabel(a.inTime.toString()),new JLabel(a.outTime==null?"---":a.outTime.toString()),new JLabel(a.ip.toString())};
            for(JLabel l:data){
                l.setFont(font);
                l.setForeground(Color.LIGHT_GRAY);
                l.setToolTipText(l.getText());
                p.add(l);
            }
        });
    }
}
