package com.bpa4j.core;

import java.util.List;

import com.bpa4j.defaults.DefaultRole;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;

public class WorkFrame{
	public static class FeatureEntry<F extends FeatureTransmissionContract>{
		private Feature<F>feature;
		public FeatureEntry(Feature<F>feature){
			this.feature=feature;
		}
		protected void render(FeatureRenderingContext ctx){
			feature.render(ctx);
		}
		public void renderPreview(FeatureRenderingContext ctx){
			feature.renderPreview(ctx);
		}
		public String toString(){
			return feature.getName();
		}
	}
	static{
		User.ftrMap.put(DefaultRole.ADMIN,User.registeredFeatures.toArray(Feature[]::new));
		User.ftrMap.put(DefaultRole.EMPTY,new Feature[0]);
	}
	@SuppressWarnings("unused")
	private FeatureEntry<?>currentFeature;
	private WorkFrameRenderer renderer;
	private User user;
	public WorkFrame(User user){
		this.renderer=ProgramStarter.getRenderingManager().getWorkFrameRenderer(this);
		this.user=user;
	}
	public List<FeatureEntry<?>>getFeatures(){
		List<FeatureEntry<?>>featureEntries=new java.util.ArrayList<>();
		Feature<?>[]features=User.ftrMap.get(getUser().role);
		for(Feature<?>f:features){
			featureEntries.add(new FeatureEntry<>(f));
		}
		return featureEntries;
	}
	public User getUser(){
		return user;
	}
	public void show(){
		renderer.showWorkFrame();
	}
	public void selectFeature(FeatureEntry<?>f){
		currentFeature=f;
		f.render(renderer.getContext(f));
	}
	public void exit(){
		ProgramStarter.exit();
	}
}