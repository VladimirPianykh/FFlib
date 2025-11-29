package com.bpa4j.defaults.features.transmission_contracts;

import com.bpa4j.feature.FeatureTransmissionContract;

public class RoleSetting implements FeatureTransmissionContract{
	private String name;
	public RoleSetting(String name){
		this.name=name;
	}
	public String getFeatureName(){
		return name;
	}
}
