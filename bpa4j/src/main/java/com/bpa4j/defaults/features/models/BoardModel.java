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
    // static final String CREATE_WITH_SUPPLIER_MESSAGE="Allowing user to create elements when element supplier is set is not possible.";
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
    private transient boolean allowDeletion;
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
        ftc.setGetAllowDeletionOp(this::getAllowDeletion);
        ftc.setSetAllowCreationOp(this::setAllowCreation);
        ftc.setSetSorterOp(this::setSorter);
        ftc.setSetFilterOp(this::setFilter);
        ftc.setRenderSorterOp(this::renderSorter);
        ftc.setRenderFilterOp(this::renderFilter);
        ftc.setSetTableCustomizerOp(this::setTableCustomizer);
        ftc.setCustomizeTableOp(this::customizeTable);
        ftc.setSetAllowDeletionOp(this::setAllowDeletion);
    }
    public Board<T> getTransmissionContract(){
        return ftc;
    }

    private ArrayList<T> getPrimaryObjects(){
        ArrayList<T> o=objects;
        if(elementSupplier!=null) o=elementSupplier.get();
        return o;
    }
    @SuppressWarnings("unchecked")
    public ArrayList<T> getObjects(){
        ArrayList<T> o=getPrimaryObjects();
        if(sorter!=null) o.sort(sorter);
        if(filter!=null){
            o=(ArrayList<T>)objects.clone();
            o.removeIf(filter.negate());
        }
        return o;
    }
    public void addObject(T object){
        getPrimaryObjects().add(object);
    }
    public void removeObject(T object){
        getPrimaryObjects().remove(object);
    }
    public void setSlicer(Function<T,String> slicer){
        // Slicer implementation would go here - handled by renderer
        //TODO: implement
    }
    public void setSorter(Board.Sorter<T> sorter){
        this.sorter=sorter;
    }
    public void setFilter(Board.Filter<T> filter){
        this.filter=filter;
    }
    public void renderSorter(RenderingContext ctx){
        if(sorter!=null) sorter.renderConfigurator(ctx);
    }
    public void renderFilter(RenderingContext ctx){
        if(filter!=null) filter.renderConfigurator(ctx);
    }
    public void setElementSupplier(Supplier<ArrayList<T>> supplier){
        // if(supplier!=null)setAllowCreation(false);
        this.elementSupplier=supplier;
    }
    public boolean getAllowCreation(){
        return allowCreation;
    }

    public boolean getAllowDeletion(){
        return allowDeletion;
    }

    public void setAllowDeletion(boolean allow){
        this.allowDeletion=allow;
    }
    public void setAllowCreation(boolean allow){
        // if(allow&&elementSupplier!=null)throw new
        // IllegalStateException(CREATE_WITH_SUPPLIER_MESSAGE);
        this.allowCreation=allow;
    }
    public void setTableCustomizer(Consumer<TableCustomizationRenderingContext> tableCustomizer){
        this.tableCustomizer=tableCustomizer;
    }
    public void customizeTable(TableCustomizationRenderingContext ctx){
        if(tableCustomizer!=null) tableCustomizer.accept(ctx);
    }
}
