package com.bpa4j.ui.swing.editor.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.Board.TableCustomizationRenderingContext;
import com.bpa4j.defaults.table.FieldCellValue;
import com.bpa4j.defaults.table.FormCellEditor;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.TableModule;
import com.bpa4j.ui.swing.SwingModularEditorRenderer.SwingModuleRenderingContext;
import com.bpa4j.ui.swing.util.HButton;

import java.awt.Graphics;

public class SwingTableModuleRenderer implements ModuleRenderer<TableModule>{
    public void createTab(Editable editable,boolean isNew,Runnable deleter,TableModule module,ModulesRenderingContext context){
        SwingModuleRenderingContext ctx=(SwingModuleRenderingContext)context;
        JDialog editor=ctx.getEditorDialog();
        try{
            @SuppressWarnings("unchecked")
            ArrayList<Object>a=(ArrayList<Object>)module.getField().get(editable);
            JPanel tab=new JPanel(new BorderLayout());
            tab.setSize(editor.getSize());
            JTable t=new JTable();
            t.setPreferredSize(new Dimension(tab.getWidth()*2/3,tab.getHeight()*2/3));
            t.setDefaultEditor(Object.class,new FormCellEditor());
            List<Field>fields=Stream.of(module.getType().getFields()).filter(f->f.isAnnotationPresent(EditorEntry.class)).toList();
            DefaultTableModel m=new DefaultTableModel(fields.stream().map(f->f.getAnnotation(EditorEntry.class).translation()).toArray(),0);
            for(Object o:a){
                FieldCellValue[]v=new FieldCellValue[fields.size()];
                for(int i=0;i<fields.size();++i)v[i]=new FieldCellValue(fields.get(i),o);
                m.addRow(v);
            }
            t.setModel(m);
            for(Consumer<TableCustomizationRenderingContext> c:module.getTableDecorators())c.accept(new SwingBoardRenderer.SwingTableCustomizationRenderingContext(t));
            JScrollPane s=new JScrollPane(t,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tab.add(s,BorderLayout.CENTER);
            HButton b=new HButton(){
                public void paint(Graphics g){
                    int c=50-scale*4;
                    if(getModel().isPressed())c-=25;
                    g.setColor(new Color(c,c,c));
                    g.fillRect(0,0,getWidth(),getHeight());
                    FontMetrics fm=g.getFontMetrics();
                    g.setColor(Color.WHITE);
                    g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
                };
            };
            b.addActionListener(e->{
                try{
                    Object o=module.getType().getDeclaredConstructor().newInstance();
                    a.add(o);
                    FieldCellValue[]v=new FieldCellValue[fields.size()];
                    for(int i=0;i<fields.size();++i)v[i]=new FieldCellValue(fields.get(i),o);
                    m.addRow(v);
                    tab.revalidate();
                }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}            
            });
            b.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
            tab.add(b,BorderLayout.SOUTH);
            ctx.getTabs().add(tab);
        }catch(IllegalAccessException ex){throw new RuntimeException(ex);}        
    }
}
