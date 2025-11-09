package com.bpa4j.core;

import java.io.File;
import java.util.List;

import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.data.file.FileStorageManager;
import com.bpa4j.editor.IEditor;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.feature.Feature;
import com.bpa4j.ui.swing.SwingRenderingManager;

/**
 * Entry point of the program.
 * To start your application, just invoke {@code runProgram()}.
 * 
 * <p>
 * 	You can also (optionally) set:
 * 	<ul>
 *    <li>{@code editor}</li>
 * 	  <li>{@code welcomeMessage}</li>
 * 	</ul>
 */
public final class ProgramStarter{
	public static class StarterContext{
		public String getWelcomeMessage(){
			return welcomeMessage;
		}
		public boolean isAuthRequired(){
			return authRequired;
		}
		/**
		 * @return 0 if logined/registered successfully, 1 if failed to login due to invalid input, 2 if failed for security reasons (wrong password)
		 */
		public int login(String login,String pass,boolean newUser){
			if(newUser){
				if(User.hasUser(login))return 1;
				else{
					User.register(login,pass);
					showWorkFrame();
					return 0;
				}
			}else{
				if(User.hasUser(login)){
					if(User.getUser(login).login(pass)){
						frame=new WorkFrame(User.getActiveUser());
						return 0;
					}else return 2;
				}else return 1;
			}
		}
		public void enterWithoutAuth(User user){
			user.login(user.password);
			showWorkFrame();
		}
	}
	private static WorkFrame frame;
	private static RenderingManager renderingManager;
	private static StorageManager storageManager;
	public static IEditor editor=new ModularEditor();
	public static String welcomeMessage;
	/**
	 * Indicates whether to require password ({@code true}) or just ask to choose the role.
	 */
	public static boolean authRequired=true;
	private ProgramStarter(){}
	/*public static void constructEditor(Editable editable,boolean isNew){
		constructEditor(editable,isNew,null);
	}
	public static void constructEditor(Editable editable,boolean isNew,Runnable deleter){
		editor.constructEditor(editable,isNew,deleter);
	}*/
	public static void runProgram(){
		if(renderingManager==null)renderingManager=new SwingRenderingManager();
		if(storageManager==null)storageManager=new FileStorageManager(new File(Root.folder));
		getRegScreen().show(new StarterContext());
	}
	public static RegScreen getRegScreen(){
		return renderingManager.getRegistrationScreen();
	}
	public static void showWorkFrame(){
		frame=new WorkFrame(User.getActiveUser());
		frame.show();
	}
	public static void exit(){
		getRenderingManager().close();
		System.exit(0);
	}

	public static RenderingManager getRenderingManager(){
		return renderingManager;
	}
	public static void setRenderingManager(RenderingManager renderingManager){
		ProgramStarter.renderingManager=renderingManager;
	}
	public static StorageManager getStorageManager(){
		return storageManager;
	}
	public static void setStorageManager(StorageManager storageManager){
		ProgramStarter.storageManager=storageManager;
	}
	
	public static void register(Role...r){
		getStorageManager().getStorage().register(r);
	}
	public static void register(Feature<?>...r){
		getStorageManager().getStorage().register(r);
	}
	public static void register(Permission...r){
		getStorageManager().getStorage().register(r);
	}
	public static void register(EditableGroup<?>...r){
		getStorageManager().getStorage().register(r);
	}
	public static void register(Role r,Feature<?>[]f,Permission[]p){
		getStorageManager().getStorage().register(r, f, p);
	}

	public static List<Role>getRegisteredRoles(){
		return getStorageManager().getStorage().getRegisteredRoles();
	}	
	public static List<Feature<?>>getRegisteredFeatures(){
		return getStorageManager().getStorage().getRegisteredFeatures();
	}
	public static List<Permission>getRegisteredPermissions(){
		return getStorageManager().getStorage().getRegisteredPermissions();
	}
	public static List<EditableGroup<?>>getRegisteredEditableGroups(){
		return getStorageManager().getStorage().getRegisteredEditableGroups();
	}
}