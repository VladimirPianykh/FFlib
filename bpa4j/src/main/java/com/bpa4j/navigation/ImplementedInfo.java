package com.bpa4j.navigation;

import java.util.ArrayList;
import java.util.List;

import com.bpa4j.navigation.HelpView.*;

public class ImplementedInfo{
    private String name;
    private List<Instruction>chain;
    public ImplementedInfo(String name){
        this(name,new ArrayList<>());
    }
    public ImplementedInfo(String name,List<Instruction>chain){
        this.name=name;
        this.chain=chain;
    }
    public ImplementedInfo appendComment(String comment){
        chain.add(new CommentInstruction(comment));
        return this;
    }
    public ImplementedInfo appendText(String text){
        chain.add(new TextInstruction(text));
        return this;
    }
    public ImplementedInfo appendInstruction(Instruction entry){
        chain.add(entry);
        return this;
    }
    public List<Instruction>getChain(){
        return chain;
    }
    public String getName(){
        return name;
    }
    public boolean equals(ImplementedInfo other){
        return name.equals(other.name);
    }
    public int hashCode(){
        return name.hashCode();
    }
    public String toString(){
        return name+": "+chain;
    }
}
