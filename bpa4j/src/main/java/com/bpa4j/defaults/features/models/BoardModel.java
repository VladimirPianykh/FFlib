package com.bpa4j.defaults.features.models;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.core.RenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.features.transmission_contracts.Board.TableCustomizationRenderingContext;
import com.bpa4j.feature.FeatureModel;

public class BoardModel<T extends Serializable> implements FeatureModel<Board<T>>{
    private Board<T> ftc;
    private ArrayList<T> objects=new ArrayList<T>(){
        private void writeObject(ObjectOutputStream out) throws IOException,ClassNotFoundException{
            if(elementSupplier!=null) clear();
            out.defaultWriteObject();
        }
    };
    private transient Supplier<ArrayList<T>> elementSupplier;
    private transient Board.Sorter<T> sorter;
    private transient Board.Filter<T> filter;
    private transient boolean allowCreation;
    private transient Consumer<Board.TableCustomizationRenderingContext> tableCustomizer;
    @SuppressWarnings("null")
    public BoardModel(Board<T> ftc){
        this.ftc=ftc;
        ftc.setGetObjectsOp(this::getObjects);
        ftc.setAddObjectOp(this::addObject);
        ftc.setRemoveObjectOp(this::removeObject);
        ftc.setSetSlicerOp(this::setSlicer);
        ftc.setSetElementSupplierOp(this::setElementSupplier);
        ftc.setGetAllowCreationOp(this::getAllowCreation);
        ftc.setSetAllowCreationOp(this::setAllowCreation);
        ftc.setSetSorterOp(this::setSorter);
        ftc.setSetFilterOp(this::setFilter);
        ftc.setRenderSorterOp(this::renderSorter);
        ftc.setRenderFilterOp(this::renderFilter);
        ftc.setSetTableCustomizerOp(this::setTableCustomizer);
        ftc.setCustomizeTableOp(this::customizeTable);
    }
    public Board<T> getTransmissionContract(){
        return ftc;
    }
    @SuppressWarnings("unchecked")
    public ArrayList<T> getObjects(){
        ArrayList<T>o=objects;
        if(elementSupplier!=null) o=elementSupplier.get();
        if(sorter!=null) o.sort(sorter);
        if(filter!=null){
            o=(ArrayList<T>)objects.clone();
            o.removeIf(filter.negate());
        }
        return o;
    }
    public void addObject(T object){
        objects.add(object);
    }
    public void removeObject(T object){
        objects.remove(object);
    }
    public void setSlicer(Function<T,String> slicer){
        // Slicer implementation would go here - handled by renderer
    }
    public void setSorter(Board.Sorter<T> sorter){
        this.sorter=sorter;
    }
    public void setFilter(Board.Filter<T> filter){
        this.filter=filter;
    }
    public void renderSorter(RenderingContext ctx){
        if(sorter!=null)sorter.renderConfigurator(ctx);
    }
    public void renderFilter(RenderingContext ctx){
        if(filter!=null)filter.renderConfigurator(ctx);
    }
    public void setElementSupplier(Supplier<ArrayList<T>> supplier){
        this.elementSupplier=supplier;
    }
    public boolean getAllowCreation(){
        return allowCreation;
    }
    public void setAllowCreation(boolean allow){
        this.allowCreation=allow;
    }
    public void setTableCustomizer(Consumer<TableCustomizationRenderingContext> tableCustomizer){
        this.tableCustomizer=tableCustomizer;
    }
    public void customizeTable(TableCustomizationRenderingContext ctx){
        if(tableCustomizer!=null) tableCustomizer.accept(ctx);
    }
}
