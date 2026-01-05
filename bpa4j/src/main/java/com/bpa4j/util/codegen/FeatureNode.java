package com.bpa4j.util.codegen;

import java.io.File;

/**
 * A node representing {@link com.bpa4j.feature.FeatureTransmissionContract Feature interface} implementation.
 */
public class FeatureNode extends ClassNode<FeatureNode>{
	public static class FeaturePhysicalNode extends ClassPhysicalNode<FeatureNode>{
		public FeaturePhysicalNode(File file){
			super(file);
		}
		@Override
		public void persist(NodeModel<FeatureNode> model){
			// FIXME: implement persist
		}
		@Override
		public NodeModel<FeatureNode> load(){
			// FIXME: implement load
			String name=file.getName();
			if(name.endsWith(".java")) name=name.substring(0,name.length()-5);
			return new FeatureModel(name);
		}
	}

	public static class FeatureModel extends ClassModel<FeatureNode>{
		public FeatureModel(String name){
			super(name);
		}
	}

	public FeatureNode(PhysicalNode<FeatureNode> physicalNode){
		super(physicalNode);
	}

	public FeatureNode(File file){
		super(new FeaturePhysicalNode(file));
	}
}
