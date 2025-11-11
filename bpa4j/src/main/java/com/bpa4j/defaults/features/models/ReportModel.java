package com.bpa4j.defaults.features.models;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JComponent;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.FeatureModel;

public class ReportModel implements FeatureModel<Report>{
    private Report ftc;
    private transient ArrayList<Supplier<JComponent>> dataRenderers=new ArrayList<>();
    private transient ArrayList<Function<Runnable,JComponent>> configurators=new ArrayList<>();
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
    public ArrayList<Supplier<JComponent>> getDataRenderers(){
        return dataRenderers;
    }
    public ArrayList<Function<Runnable,JComponent>> getConfigurators(){
        return configurators;
    }
    public void addDataRenderer(Supplier<JComponent> dataRenderer){
        dataRenderers.add(dataRenderer);
    }
    public void addConfigurator(Function<Runnable,JComponent> configurator){
        configurators.add(configurator);
    }
}
