package com.bpa4j.ui.rest.abstractui;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UI state.
 * Must be invalidated in order to be updated by server.
 * </p>
 * The UI system itself is not thread-safe, but this class performs all operations in the EDT.
 * Operations with components are allowed to be performed in <i>other</i> single thread <b>as long as they are not plugged</b> in this {@code UIState}.
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
	private List<Window> windows=new ArrayList<>();
	private Window activeWindow;
	private boolean valid=true;
	public void callFunction(String id){
		invokeAndWait(()->{
			if(activeWindow!=null)activeWindow.callFunction(id);
		});
	}
	public void modifyComponent(String id,Map<String,Object>update){
		invokeAndWait(()->activeWindow.modifyComponent(id,update));
	}
	public Map<String,Object>getJsonWithValidity(){
		return invokeAndWait(()->{
			boolean validNow=isValid();
			return Map.of(
				"valid",validNow,
				"structure",getJsonAndMarkValid()
			);
		});
	}
	/**
	 * Must only be called by the server, which updates UI.
	 */
	public Map<String,Object>getJsonAndMarkValid(){
		return invokeAndWait(()->{
			valid=true;
			//Window absence can be handled in a different way.
			if(activeWindow==null)throw new IllegalStateException("No window shown.");
			return activeWindow.getJson();
		});
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
	public<T>T invokeAndWait(Callable<T>runnable){
		try{
			if(Thread.currentThread()==edtThread){
				try{
					return runnable.call();
				}catch(Exception exception){
					throw new ExecutionException(exception);
				}
			}else return edt.submit(runnable).get();
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
		invokeLater(()->{
			activeWindow=w;
			if(windows.contains(w))windows.remove(w);
			windows.add(w);
			w.update();
			invalidate();
		});
	}
	public void close(Window w){
		invokeLater(()->{
			windows.remove(w);
			if(activeWindow==w)activeWindow=windows.isEmpty()?null:windows.getLast();
		});
	}
}
