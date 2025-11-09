package com.bpa4j.core;

import java.util.function.Supplier;
import com.bpa4j.feature.FeatureModel;
import com.bpa4j.feature.FeatureSaver;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Factory for persistent storage providers.
 */
public interface StorageManager{
	boolean isFirstLaunch();
	<F extends FeatureTransmissionContract>FeatureModel<F>getFeatureModel(F f);
	<F extends FeatureTransmissionContract>FeatureSaver<F>getFeatureSaver(F f);
	<F extends FeatureTransmissionContract>void putModel(Class<F>f,Supplier<FeatureModel<F>>model);
	<F extends FeatureTransmissionContract>void putSaver(Class<F>f,Supplier<FeatureSaver<F>>saver);
	Data getStorage();
	UserSaver getUserSaver();
	void close();
}
