package com.bpa4j.ui.rest;

import java.util.List;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.components.ComboBox;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.ProgressBar;
import com.bpa4j.ui.rest.abstractui.components.TextArea;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

public class AbstractUIDemoServerMain{
	public static void main(String[]args) throws Exception{
		UIState state=new UIState();

		Panel root=new Panel(new FlowLayout());
		root.setSize(1000,1000);

		Label label=new Label("Demo UI Components");
		TextField textField=new TextField("Hello");
		TextArea textArea=new TextArea("Multiline text\nsecond line");
		Button button=new Button("Click me");
		CheckBox checkBox=new CheckBox("Check me");
		ComboBox comboBox=new ComboBox(List.of("One","Two","Three"));
		ProgressBar progressBar=new ProgressBar();
		progressBar.setValue(50);

		root.add(label);
		root.add(textField);
		root.add(textArea);
		root.add(button);
		root.add(checkBox);
		root.add(comboBox);
		root.add(progressBar);

		Window window=new Window(root);

		state.invokeAndWait(()->{
			state.showWindow(window);
			state.invalidate();
		});

		UIServer server=new UIServer(state,5617);
		server.start();
		Thread.startVirtualThread(()->{
			try{
				Thread.sleep(10000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			state.invalidate();
		});
		System.out.println("AbstractUI demo server started on http://localhost:5617/ui/structure");
		System.out.println("Press ENTER to stop...");
		try{
			System.in.read();
		}finally{
			server.stop();
		}
	}
}
