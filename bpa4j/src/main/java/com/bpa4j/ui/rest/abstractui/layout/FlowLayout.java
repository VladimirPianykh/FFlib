package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Point;
import com.bpa4j.ui.rest.abstractui.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Простая и предсказуемая реализация FlowLayout.
 *
 * Основные принципы:
 *  1) Размещение компонентов делится на два независимых этапа:
 *      - сначала вычисляется разбиение на строки (для LTR/RTL)
 *        или колонки (для TTB/BTT)
 *      - затем строки/колонки выравниваются и расставляются
 *
 *  2) Размещение использует preferredSize(), определённый в этом
 *     layout-е, а не comp.getSize(). Это исключает накапливание ошибок
 *     и некорректное сжатие мелких элементов.
 *
 *  3) FlowLayout НЕ изменяет preferredSize() компонентов.
 *     Компонент получает ровно такой размер, какой запрошен.
 *
 *  4) Полная предсказуемость: никакого fallback на “800×600”.
 *
 * Расширяемость:
 *   - метод getPreferredSize(Component) можно переопределить,
 *     чтобы подключить предпочитаемые размеры из ваших UI-компонентов.
 */
public class FlowLayout implements LayoutManager{

    public static final int LEFT=0;
    public static final int CENTER=1;
    public static final int RIGHT=2;

    public static final int LTR=0;
    public static final int RTL=1;
    public static final int TTB=2;
    public static final int BTT=3;

    private int align;
    private int direction;
    private int hgap;
    private int vgap;

    public FlowLayout(){
        this(CENTER,LTR,5,5);
    }

    public FlowLayout(int align){
        this(align,LTR,5,5);
    }

    public FlowLayout(int align,int direction,int hgap,int vgap){
        this.align=align;
        this.direction=direction;
        this.hgap=hgap;
        this.vgap=vgap;
    }

    /**
     * Возвращает предпочитаемый размер компонента.
     * По умолчанию — фиксированное значение (150×50).
     * Можно переопределить, чтобы использовать реальные предпочтения.
     */
    protected Size getPreferredSize(Component comp){
        return new Size(150,50);
    }

    @Override
    public void layout(Panel target){
        int pw=target.getWidth();
        int ph=target.getHeight();
        if(pw<=0||ph<=0){
            // Нет смысла раскладывать, если панель ещё не измерена.
            return;
        }

        if(direction==LTR||direction==RTL){
            layoutHorizontal(target,pw,ph);
        }else{
            layoutVertical(target,pw,ph);
        }
    }

    /* ========================  HORIZONTAL FLOW  ======================== */

    private static class Row{
        final List<Component> comps=new ArrayList<>();
        int width=0;
        int height=0;
    }

    private void layoutHorizontal(Panel target,int pw,int ph){
        List<Row> rows=new ArrayList<>();
        Row current=new Row();

        for(Component c:target.getComponents()){
            Size ps=getPreferredSize(c);

            int needed=(current.comps.isEmpty()?0:current.width+hgap)+ps.width();
            boolean newRow=needed>pw;

            if(newRow&&!current.comps.isEmpty()){
                rows.add(current);
                current=new Row();
            }

            current.comps.add(c);
            current.width=(current.comps.size()==1)?ps.width():current.width+hgap+ps.width();
            current.height=Math.max(current.height,ps.height());
        }

        if(!current.comps.isEmpty()){
            rows.add(current);
        }

        /* === Расстановка === */
        int y=0;
        for(Row row:rows){
            int x;
            if(align==LEFT){
                x=0;
            }else if(align==CENTER){
                x=(pw-row.width)/2;
            }else{ // RIGHT
                x=pw-row.width;
            }

            if(direction==RTL){
                x=pw-row.width;
            }

            for(Component c:row.comps){
                Size ps=getPreferredSize(c);
                int cx=x;
                int cy=y;

                if(direction==RTL){
                    // размещаем справа налево
                    cx=x+(row.width-ps.width());
                }

                c.setLocation(new Point(cx,cy));
                c.setSize(ps);

                x+=ps.width()+hgap;
            }

            y+=row.height+vgap;
        }
    }

    /* ========================  VERTICAL FLOW  ======================== */

    private static class Column{
        final List<Component> comps=new ArrayList<>();
        int width=0;
        int height=0;
    }

    private void layoutVertical(Panel target,int pw,int ph){
        List<Column> cols=new ArrayList<>();
        Column current=new Column();

        for(Component c:target.getComponents()){
            Size ps=getPreferredSize(c);

            int needed=(current.comps.isEmpty()?0:current.height+vgap)+ps.height();
            boolean newCol=needed>ph;

            if(newCol&&!current.comps.isEmpty()){
                cols.add(current);
                current=new Column();
            }

            current.comps.add(c);
            current.height=(current.comps.size()==1)?ps.height():current.height+vgap+ps.height();
            current.width=Math.max(current.width,ps.width());
        }

        if(!current.comps.isEmpty()){
            cols.add(current);
        }

        /* === Расстановка === */
        int x=0;
        for(Column col:cols){
            int y;
            if(align==LEFT){
                y=0;
            }else if(align==CENTER){
                y=(ph-col.height)/2;
            }else{
                y=ph-col.height;
            }

            if(direction==BTT){
                y=ph-col.height;
            }

            for(Component c:col.comps){
                Size ps=getPreferredSize(c);
                int cx=x;
                int cy=y;

                if(direction==BTT){
                    cy=y+(col.height-ps.height());
                }

                c.setLocation(new Point(cx,cy));
                c.setSize(ps);

                y+=ps.height()+vgap;
            }

            x+=col.width+hgap;
        }
    }

    /* ========================  getters/setters  ======================== */

    public int getAlignment(){
        return align;
    }
    public void setAlignment(int align){
        this.align=align;
    }
    public int getHgap(){
        return hgap;
    }
    public void setHgap(int hgap){
        this.hgap=hgap;
    }
    public int getVgap(){
        return vgap;
    }
    public void setVgap(int vgap){
        this.vgap=vgap;
    }
    public int getDirection(){
        return direction;
    }
    public void setDirection(int direction){
        this.direction=direction;
    }
}
