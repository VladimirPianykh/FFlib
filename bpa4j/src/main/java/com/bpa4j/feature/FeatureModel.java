package com.bpa4j.feature;

/**
 * Feature model instance, bound to {@link FeatureTransmissionContract}.
 * Should get the contract via constructor.
 */
public interface FeatureModel<T extends FeatureTransmissionContract>{
    T getTransmissionContract();
}
