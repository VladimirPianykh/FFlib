package com.bpa4j.ui.rest;

import org.junit.jupiter.api.Test;

import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.google.gson.Gson;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.components.TextArea;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.components.ComboBox;
import com.bpa4j.ui.rest.abstractui.components.ProgressBar;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AbstractUITest{
	@Test
	public void testServer()throws Exception{
		UIState state=new UIState();
		UIServer server=new UIServer(state);
		// Window window=new Window(root);
		Thread.sleep(100);
		HttpClient c=HttpClient.newHttpClient();
		HttpRequest req=HttpRequest.newBuilder(URI.create("https://localhost:5617/ui/structure")).GET().build();
		HttpResponse<String>res=c.send(req,BodyHandlers.ofString());
		Gson gson=new Gson();
		//TODO: finish test
		// gson.toJsonTree(gson.fromJson(res.body(),TreeMap.class)).a
		server.stop();
	}
}
