package com.bpa4j.util.codegen;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

/**
 * A node representing {@link com.bpa4j.feature.FeatureTransmissionContract Feature interface} implementation.
 */
public class FeatureNode extends ClassNode<FeatureNode>{
	public static interface FeaturePhysicalNode extends ClassPhysicalNode<FeatureNode>{}
	public static class FileFeaturePhysicalNode extends FileClassPhysicalNode<FeatureNode> implements FeaturePhysicalNode{
		public FileFeaturePhysicalNode(File file){
			super(file);
		}
		@Override
		public void persist(NodeModel<FeatureNode> model){
			if(file.exists()) throw new IllegalStateException("Physical representation already exists: "+file.getAbsolutePath());
			try{
				if(file.getParentFile()!=null) file.getParentFile().mkdirs();
				String className=file.getName().replace(".java","");
				String s="public class "+className+" {}";
				Files.writeString(file.toPath(),s);
			}catch(IOException ex){
				throw new java.io.UncheckedIOException(ex);
			}
		}
		@Override
		public FeatureModel load(){
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

	public FeatureNode(FeaturePhysicalNode physicalNode){
		super(physicalNode);
	}
}
