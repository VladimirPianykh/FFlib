package com.bpa4j.feature;

import java.io.Serializable;

import com.bpa4j.navigation.TaskLoc;

/**
 * A reverse MVC controller.
 * Defines contract, which is used by model, renderer and saver.
 * {@link FeatureModel} subscribes on this object's events and {@link FeatureRenderer} calls it to get data from the model.
 * No direct data binding occurs: this object is always in the middle.
 * It is channel in PubSub event model.
 * </p>
 * All information transitions is made via operations.
 * Operations are fields with "Op" ending,
 * and each of them <b>must</b> have a setter.
 * This is necessary.
 * Also, you might want to define a method which delegate to operation directly.
 */
public interface FeatureTransmissionContract extends Serializable,TaskLoc{
	public String getFeatureName();
}