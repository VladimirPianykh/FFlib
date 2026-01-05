package com.bpa4j.util.codegen;

import java.io.File;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class EditableGroupNode implements ProjectNode<EditableGroupNode>{
	public static class EditableGroupPhysicalNode implements PhysicalNode<EditableGroupNode>{
		private final File file;
		public EditableGroupPhysicalNode(File file){
			this.file=file;
		}
		@Override
		public void clear(){
			file.delete();
		}
		@Override
		public boolean exists(){
			return file.exists();
		}
		@Override
		public void persist(NodeModel<EditableGroupNode> model){
		}
		@Override
		public NodeModel<EditableGroupNode> load(){
			return new EditableGroupModel();
		}
	}
	
	public static class EditableGroupModel implements NodeModel<EditableGroupNode>{
	}
	
	private final PhysicalNode<EditableGroupNode> physicalNode;
	private final NodeModel<EditableGroupNode> model;

	public EditableGroupNode(PhysicalNode<EditableGroupNode> physicalNode){
		this.physicalNode=physicalNode;
		this.model=physicalNode.load();
	}
	
	public EditableGroupNode(File location,ObjectCreationExpr constructor){
		this(new EditableGroupPhysicalNode(location));
	}
	
	@Override
	public PhysicalNode<EditableGroupNode> getPhysicalRepresentation(){
		return physicalNode;
	}
	@Override
	public NodeModel<EditableGroupNode> getModel(){
		return model;
	}
}
