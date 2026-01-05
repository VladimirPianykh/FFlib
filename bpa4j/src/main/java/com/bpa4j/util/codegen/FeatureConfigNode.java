package com.bpa4j.util.codegen;

import java.io.File;
import lombok.Getter;

/**
 * A node representing {@code Feature} registration and/or configuration.
 */
public class FeatureConfigNode implements ProjectNode<FeatureConfigNode>{
	public static class FeatureConfigPhysicalNode implements PhysicalNode<FeatureConfigNode>{
		private final File file;
		public FeatureConfigPhysicalNode(File file){
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
		public void persist(NodeModel<FeatureConfigNode> model){
			// FIXME Parse FeatureConfigNode
		}
		@Override
		public NodeModel<FeatureConfigNode> load(){
			// FIXME Parse FeatureConfigNode
			return new FeatureConfigModel(null);
		}
	}

	public static class FeatureConfigModel implements NodeModel<FeatureConfigNode>{
		@Getter
		private final String featureName;
		public FeatureConfigModel(String featureName){
			this.featureName=featureName;
		}
	}

	private final PhysicalNode<FeatureConfigNode> physicalNode;
	private final NodeModel<FeatureConfigNode> model;

	public FeatureConfigNode(PhysicalNode<FeatureConfigNode> physicalNode){
		this.physicalNode=physicalNode;
		this.model=physicalNode.load();
	}

	public FeatureConfigNode(File file,String featureName){
		this(new FeatureConfigPhysicalNode(file));
		// Override loaded model with passed featureName (creation time)
		// But in original logic: "this.featureName = featureName; // TODO parse"
		// The original logic saved featureName in field but didn't use it.
	}

	@Override
	public PhysicalNode<FeatureConfigNode> getPhysicalRepresentation(){
		return physicalNode;
	}
	@Override
	public NodeModel<FeatureConfigNode> getModel(){
		return model;
	}
}
