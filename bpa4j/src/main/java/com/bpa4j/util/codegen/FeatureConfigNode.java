package com.bpa4j.util.codegen;

import java.io.File;

import lombok.Getter;

/**
 * A node representing {@code Feature} registration and/or configuration.
 */
public class FeatureConfigNode extends ProjectNode{
	@Getter
	private String featureName;
	public FeatureConfigNode(File location,String featureName){
		super(location);
		this.featureName=featureName;
		//TODO: parse
	}
}
