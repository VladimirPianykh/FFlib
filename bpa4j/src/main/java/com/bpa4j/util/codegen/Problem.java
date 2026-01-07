package com.bpa4j.util.codegen;

class Problem{
	public enum ProblemType{
		ERROR,WARNING,INFO
	}
	public final String message;
	public final ProblemType type;
	public final Runnable solver;
	public Problem(String message,ProblemType type){
		this.message=message;
		this.type=type;
		this.solver=null;
	}
	public Problem(String message,ProblemType type,Runnable solver){
		this.message=message;
		this.type=type;
		this.solver=solver;
	}
}