package com.bpa4j.util.codegen;

import java.io.File;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class EditableGroupNode extends ProjectNode{
    public EditableGroupNode(File location,ObjectCreationExpr constructor){
        super(location);
        
    }
}
