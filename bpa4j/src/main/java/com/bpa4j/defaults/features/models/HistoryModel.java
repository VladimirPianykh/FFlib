package com.bpa4j.defaults.features.models;

import com.bpa4j.defaults.features.transmission_contracts.History;
import com.bpa4j.feature.FeatureModel;

public class HistoryModel implements FeatureModel<History>{
    private History ftc;
    public HistoryModel(History ftc){
        this.ftc=ftc;
    }
    public History getTransmissionContract(){
        return ftc;
    }
}
