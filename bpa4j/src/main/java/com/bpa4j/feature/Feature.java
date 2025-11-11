package com.bpa4j.feature;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.navigation.ImplementedInfo;

/**
 * {@code Feature} is an item with it's own user-available functionality, which is accessible from the {@link WorkFrame} UI.
 * Consists of {@link FeatureTransmissionContract}, {@link FeatureModel}, {@link FeatureRenderer} and {@link FeatureSaver}.
 */
public class Feature<F extends FeatureTransmissionContract>{
    private F contract;
    private FeatureModel<F>model;
    private FeatureRenderer<F>renderer;
    private FeatureSaver<F>saver;
    /**
     * Creates {@code Feature} by generating components from the contract.
     */
    public Feature(F contract){
        this.contract=contract;
        model=ProgramStarter.getStorageManager().getFeatureModel(contract);
        saver=ProgramStarter.getStorageManager().getFeatureSaver(contract);
        renderer=ProgramStarter.getRenderingManager().getFeatureRenderer(contract);
    }
    public Feature(F contract,FeatureModel<F>model,FeatureRenderer<F>renderer,FeatureSaver<F>saver){
        this.contract=contract;
        this.model=model;
        this.renderer=renderer;
        this.saver=saver;
    }
    public F getContract(){
        return contract;
    }
    public void render(FeatureRenderingContext ctx){
        renderer.render(ctx);
    }
    public void save(){
        saver.save(contract);
    }
    FeatureModel<F>getModel(){
        return model;
    }
    FeatureRenderer<F>getRenderer(){
        return renderer;
    }
    FeatureSaver<F>getSaver(){
        return saver;
    }
	public Iterable<ImplementedInfo>getImplementedInfo(){
		return getContract().getImplementedInfo();
	}
	public void renderPreview(FeatureRenderingContext ctx){
		renderer.renderPreview(ctx);
	}
    public String getName(){
        return contract.getFeatureName();
    }
}
