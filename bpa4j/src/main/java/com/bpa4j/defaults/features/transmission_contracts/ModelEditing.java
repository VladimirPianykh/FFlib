package com.bpa4j.defaults.features.transmission_contracts;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Standard model editing tab with all registered editable groups.
 * Unlike other operations, {@link #getRenderingContextOp} is provided by the renderer.
 */
public class ModelEditing implements FeatureTransmissionContract{
	public Supplier<FeatureRenderingContext>getRenderingContextOp;
	public BiConsumer<Integer,Editable>createOp;
	public Consumer<Editable>editOp;
	public BiConsumer<Integer,Editable>deleteOp;
	public Supplier<List<EditableGroup<?>>> getGroupsOp;
	
	public void setCreateOp(BiConsumer<Integer,Editable> createOp){
		this.createOp=createOp;
	}
	public void setGetRenderingContextOp(Supplier<FeatureRenderingContext> getRenderingContextOp){
		this.getRenderingContextOp=getRenderingContextOp;
	}
	public void setEditOp(Consumer<Editable> editOp){
		this.editOp=editOp;
	}
	public void setDeleteOp(BiConsumer<Integer,Editable> deleteOp){
		this.deleteOp=deleteOp;
	}
	public void setGetGroupsOp(Supplier<List<EditableGroup<?>>> getGroupsOp){
		this.getGroupsOp=getGroupsOp;
	}

	public void create(int groupIndex,Editable e){
		createOp.accept(groupIndex,e);
		ProgramStarter.editor.constructEditor(e,true,getRenderingContext());
	}
	public void edit(Editable e){
		editOp.accept(e);
		ProgramStarter.editor.constructEditor(e,true,getRenderingContext());
	}
	public void delete(int groupIndex,Editable e){
		deleteOp.accept(groupIndex,e);
	}
	public FeatureRenderingContext getRenderingContext(){
		return getRenderingContextOp.get();
	}
	public String getFeatureName(){
		return "Редактирование данных";
	}
	public List<EditableGroup<?>> getGroups(){
		return getGroupsOp.get();
	}
}
