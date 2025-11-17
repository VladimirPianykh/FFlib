package com.bpa4j.ui.rest.abstractui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UI state.
 * Must be invalidated in order to be updated by server.
 * </p>
 * The UI system itself is not thread-safe, but this class performs all operations in the EDT.
 */
public class UIState{
	private ExecutorService edt=Executors.newSingleThreadExecutor();
	private Thread edtThread;
	{
		try{
			edtThread=edt.submit(()->Thread.currentThread()).get();
		}catch(InterruptedException|ExecutionException ex){
			throw new IllegalStateException(ex);
		}
	}
	private List<Window>windows;
	// private Map<String,Component>functions;
	private Window activeWindow;
	private boolean valid=true;
	/**
	 * Must only be called by the server, which updates UI.
	 */
	public Map<String,Object>getJsonAndMarkValid(){
		invokeLater(()->valid=true);
		return activeWindow.getJson();
	}
	public void invalidate(){
		invokeLater(()->valid=false);
	}
	public boolean isValid(){
		boolean[]b={false};
		invokeAndWait(()->b[0]=valid);
		return b[0];
	}
	public void invokeAndWait(Runnable runnable){
		try{
			if(Thread.currentThread()==edtThread){
				try{
					runnable.run();
				}catch(Exception exception){
					throw new ExecutionException(exception);
				}
			}else edt.submit(runnable).get();
		}catch(InterruptedException ex){
			throw new IllegalStateException("The EDT may not be interrupted.",ex);
		}catch(ExecutionException ex){
			throw new RuntimeException(ex);
		}
	}
	public void invokeLater(Runnable runnable){
		edt.submit(runnable);
	}
	public void showWindow(Window w){
		activeWindow=w;
		if(windows.contains(w))windows.remove(w);
		windows.add(w);
	}
}
