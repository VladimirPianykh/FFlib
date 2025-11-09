package com.bpa4j.feature;

public interface FeatureSaver<F extends FeatureTransmissionContract>{
    void save(F f);
    void load(F f);
}
