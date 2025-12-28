package com.bpa4j.feature;

import java.io.Serializable;

import com.bpa4j.navigation.TaskLoc;

/**
 * A reverse MVC controller.
 * Defines contract, which is used by model, renderer and saver.
 * {@link FeatureModel} subscribes on this object's events and {@link FeatureRenderer} calls it to get data from the model.
 * No direct data binding occurs: this object is always in the middle.
 * It is a channel in PubSub event model.
 */
public interface FeatureTransmissionContract extends Serializable,TaskLoc{
	public String getFeatureName();
}