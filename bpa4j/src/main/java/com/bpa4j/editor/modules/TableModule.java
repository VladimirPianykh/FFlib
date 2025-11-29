package com.bpa4j.editor.modules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Consumer;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.features.transmission_contracts.Board.TableCustomizationRenderingContext;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;

/**
 * A module that displays a table of a particular field of the given type.
 */
public class TableModule implements EditorModule{
    private Field f;
    private Class<?> type;
    private ArrayList<Consumer<Board.TableCustomizationRenderingContext>> tableDecorators=new ArrayList<>();
    private java.util.function.Function<EditorModule,ModuleRenderer<TableModule>> rendererSource;
    /**
     * Constructs a new TableModule for the given field.
     * @param f - field to make table for
     * @param type - type of the {@code f} field
     */
    public TableModule(Field f,Class<?>type){this.f=f;this.type=type;}
    /**
     * <p>Adds a table decorator to this module.</p>
     * <p>
     * Table decorators will process the table additionaly upon creation.
     * </p>
     * @param decorator - table decorator to be used
     */
    public TableModule addTableDecorator(Consumer<Board.TableCustomizationRenderingContext>decorator){
        if(tableDecorators==null)tableDecorators=new ArrayList<>();
        tableDecorators.add(decorator);
        return this;
    }
    public Field getField(){
        return f;
    }
    public Class<?> getType(){
        return type;
    }
    public ArrayList<Consumer<TableCustomizationRenderingContext>> getTableDecorators(){
        return tableDecorators;
    }
    public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
        if(rendererSource!=null){
            rendererSource.apply(this).createTab(editable,isNew,deleter,this,ctx);
        }
    }
    @SuppressWarnings("unchecked")
    public <M extends EditorModule> void setRendererSource(java.util.function.Function<M,? extends ModuleRenderer<M>> rendererSource){
        this.rendererSource=(java.util.function.Function<EditorModule,ModuleRenderer<TableModule>>)rendererSource;
    }
}
