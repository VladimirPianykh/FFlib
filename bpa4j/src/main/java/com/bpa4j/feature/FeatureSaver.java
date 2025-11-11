package com.bpa4j.feature;

/**
 * Saves and loads the feature's data.
 * Ignores the feature's configuration (transient fields like element suppliers, slicers, sorters, etc).
 */
public interface FeatureSaver<F extends FeatureTransmissionContract>{
    void save(F f);
    void load(F f);
}
