package com.bpa4j.defaults.features.models;

import java.util.ArrayList;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.FeatureModel;

public class ReportModel implements FeatureModel<Report>{
    private Report ftc;
    private transient ArrayList<Report.DataRenderer> dataRenderers=new ArrayList<>();
    private transient ArrayList<Report.Configurator> configurators=new ArrayList<>();
    public ReportModel(Report ftc){
        this.ftc=ftc;
        ftc.setGetDataRenderersOp(()->getDataRenderers());
        ftc.setGetConfiguratorsOp(()->getConfigurators());
        ftc.setAddDataRendererOp((renderer)->addDataRenderer(renderer));
        ftc.setAddConfiguratorOp((configurator)->addConfigurator(configurator));
    }
    public Report getTransmissionContract(){
        return ftc;
    }
    public ArrayList<Report.DataRenderer> getDataRenderers(){
        return dataRenderers;
    }
    public ArrayList<Report.Configurator> getConfigurators(){
        return configurators;
    }
    public void addDataRenderer(Report.DataRenderer dataRenderer){
        dataRenderers.add(dataRenderer);
    }
    public void addConfigurator(Report.Configurator configurator){
        configurators.add(configurator);
    }
}
