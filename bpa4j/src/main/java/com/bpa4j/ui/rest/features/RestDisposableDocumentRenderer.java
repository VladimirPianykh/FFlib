package com.bpa4j.ui.rest.features;

import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.DisposableDocument;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

public class RestDisposableDocumentRenderer<T extends Editable> implements FeatureRenderer<DisposableDocument<T>>{
	private final DisposableDocument<T> contract;
	private T currentDocument;
	
	public RestDisposableDocumentRenderer(DisposableDocument<T> contract){
		this.contract=contract;
	}
	public DisposableDocument<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}
		target.setLayout(new FlowLayout(FlowLayout.CENTER,FlowLayout.CENTER,10,10));
		
		Button createBtn=new Button(currentDocument==null?"Create Document":"Edit Document");
		createBtn.setOnClick(b->{
			if(currentDocument==null){
				currentDocument=contract.createDocument();
			}
			ProgramStarter.editor.constructEditor(currentDocument,true,()->currentDocument=null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			rctx.rebuild();
		});
		target.add(createBtn);
		
		Button confirmBtn=new Button("Confirm");
		confirmBtn.setOnClick(b->{
			if(currentDocument!=null){
				contract.processDocument(currentDocument);
				currentDocument=null;
				rctx.rebuild();
			}
		});
		target.add(confirmBtn);
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
