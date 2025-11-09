package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Role;
import com.bpa4j.defaults.features.transmission_contracts.RoleSetting;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.WorkTabButton;

public class RoleSettingRenderer implements FeatureRenderer<RoleSetting>{
    private RoleSetting contract;
    public RoleSettingRenderer(RoleSetting contract){
        this.contract=contract;
    }
    public RoleSetting getTransmissionContract(){
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
        g2.setStroke(new BasicStroke(h/15,2,0));
        g2.drawOval(h/3,h/3-h/6,h/3,h/3);
        g2.drawPolyline(new int[]{h/4,h/2-h/10,h/2+h/10,h*3/4},new int[]{h,h/2+h/15,h/2+h/15,h},4);
    }
    public void fillTab(JPanel content,JPanel tab,Font font){
        JPanel p=WorkTabButton.createTable(User.getUserCount(),5,tab,true);
        Role[] roles=User.registeredRoles.toArray(new Role[0]);
        User.forEachUser(u->{
            JLabel name=new JLabel(u.login);
            name.setFont(font);
            name.setForeground(Color.WHITE);
            JComboBox<Role> b=new JComboBox<Role>(roles);
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
}
