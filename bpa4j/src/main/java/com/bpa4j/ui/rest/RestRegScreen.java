package com.bpa4j.ui.rest;

import java.util.ArrayList;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.RegScreen;
import com.bpa4j.core.User;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.ComboBox;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * @author AI-generated
 */
public class RestRegScreen implements RegScreen{
	private final UIState state;
	public RestRegScreen(UIState state){
		this.state=state;
	}
	public void show(ProgramStarter.StarterContext context){
		Panel root=new Panel(new GridLayout(1,1,5,5));
		root.setSize(RestRenderingManager.DEFAULT_SIZE);
		Panel content=new Panel(new FlowLayout(FlowLayout.CENTER,FlowLayout.TTB,10,10));
		root.add(content);
		Label welcomeLabel=new Label(context.getWelcomeMessage());
		content.add(welcomeLabel);
		Label statusLabel=new Label("");
		content.add(statusLabel);
		if(context.isAuthRequired()){
			TextField loginField=new TextField("Login");
			TextField passwordField=new TextField("Password");
			// Assuming TextField doesn't have a specific password mode based on available components, 
			// or if it does, it's not visible in the file list. Using standard TextField.

			content.add(new Label("Login:"));
			content.add(loginField);
			content.add(new Label("Password:"));
			content.add(passwordField);

			Button loginButton=new Button("Login");
			Button registerButton=new Button("Register");
			loginButton.setBackground(RestTheme.ACCENT);
			loginButton.setForeground(RestTheme.ON_ACCENT);
			registerButton.setBackground(RestTheme.ACCENT);
			registerButton.setForeground(RestTheme.ON_ACCENT);
			loginButton.setOnClick(b->{
				String login=loginField.getText();
				String password=passwordField.getText();
				if(login.isBlank()||password.isBlank()) return;

				int result=context.login(login,password,false);
				handleLoginResult(result,false,statusLabel);
			});

			registerButton.setOnClick(b->{
				String login=loginField.getText();
				String password=passwordField.getText();
				if(login.isBlank()||password.isBlank()) return;

				int result=context.login(login,password,true);
				handleLoginResult(result,true,statusLabel);
			});

			content.add(loginButton);
			content.add(registerButton);

		}else{
			ComboBox userCombo=new ComboBox();
			ArrayList<String> users=new ArrayList<>();
			User.forEachUser(u->users.add(u.toString()));
			userCombo.setItems(users);
			content.add(userCombo);

			Button confirmButton=new Button("Confirm");
			confirmButton.setBackground(RestTheme.ACCENT);
			confirmButton.setForeground(RestTheme.ON_ACCENT);
			confirmButton.setOnClick(b->{
				User selected=User.getUser(userCombo.getSelectedItem());
				if(selected!=null)context.enterWithoutAuth(selected);
			});
			content.add(confirmButton);
		}
		Window window=new Window(root);
		state.showWindow(window);
	}
	private void handleLoginResult(int result,boolean isRegistration,Label statusLabel){
		if(result==0){
			statusLabel.setText(isRegistration?"User registered.":"Login successful.");
			// Proceed? context.login usually sets the user in the context or similar.
			// The Swing impl disposes the frame.
			// Here we might want to update UI or let the caller handle it.
		}else if(result==1){
			statusLabel.setText(isRegistration?"Account already exists.":"Unknown user.");
		}else if(result==2){
			statusLabel.setText("Invalid password.");
		}
	}
}
