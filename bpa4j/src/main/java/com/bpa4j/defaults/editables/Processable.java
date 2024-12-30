package com.bpa4j.defaults.editables;

import java.io.Serializable;

import com.bpa4j.core.User;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.User.Permission;

public abstract class Processable extends Editable{
	public static class Stage implements Serializable{
		public String name;
		public Permission approver,rejecter;
		public int rejectionIndex;
		public Stage(String name,Permission approver){
			this.name=name;
			this.approver=approver;
		}
		public Stage(String name,Permission approver,Permission rejecter,int rejectionIndex){
			this.name=name;
			this.approver=approver;
			this.rejecter=rejecter;
			this.rejectionIndex=rejectionIndex;
		}
	}
	public Stage[]stages;
	public int currentStage;
	public Processable(String name,Stage...stages){super(name);this.stages=stages;}
	public Stage getStage(){return stages[currentStage];}
	public boolean isLastStage(){return currentStage==stages.length-1;}
	public void approve(String comment){
		if(isLastStage())throw new IllegalStateException("Cannot approve an object with the last stage.");
		records.add(new ActionRecord('+'+getStage().name,User.getActiveUser()));
		++currentStage;
		if(!comment.isBlank())records.add(new ActionRecord('>'+User.getActiveUser().login+':'+comment,User.getActiveUser()));
	}
	public void reject(String comment){
		currentStage=getStage().rejectionIndex;
		records.add(new ActionRecord('-'+getStage().name,User.getActiveUser()));
		if(!comment.isBlank())records.add(new ActionRecord('>'+User.getActiveUser().login+':'+comment,User.getActiveUser()));
	}
}
