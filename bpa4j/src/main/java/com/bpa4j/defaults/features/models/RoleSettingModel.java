package com.bpa4j.defaults.features.models;

import com.bpa4j.defaults.features.transmission_contracts.RoleSetting;
import com.bpa4j.feature.FeatureModel;

public class RoleSettingModel implements FeatureModel<RoleSetting>{
	private RoleSetting ftc;
	public RoleSettingModel(RoleSetting ftc){
		this.ftc=ftc;
	}
	public RoleSetting getTransmissionContract(){
		return ftc;
	}
}
