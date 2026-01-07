package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import lombok.Getter;

/**
 * A node representing {@code Feature} registration and/or configuration.
 *
 * @author AI-generated
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
			if(file.exists()){ throw new IllegalStateException("Physical representation already exists: "+file.getAbsolutePath()); }
			try{
				if(file.getParentFile()!=null) file.getParentFile().mkdirs();
				String className=file.getName().replace(".java","");
				String s="public class "+className+" {}";
				Files.writeString(file.toPath(),s);
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
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

	@Override
	public PhysicalNode<FeatureConfigNode> getPhysicalRepresentation(){
		return physicalNode;
	}
	@Override
	public NodeModel<FeatureConfigNode> getModel(){
		return model;
	}
}
