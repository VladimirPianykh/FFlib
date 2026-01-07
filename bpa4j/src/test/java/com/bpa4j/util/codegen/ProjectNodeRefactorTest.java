package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.bpa4j.util.codegen.ProjectNode.NodeModel;
import com.bpa4j.util.codegen.ProjectNode.PhysicalNode;

public class ProjectNodeRefactorTest {

	@TempDir
	File tempDir;

	@Test
	public void testClassNodeStructure() throws IOException {
		File file=new File(tempDir,"TestClass.java");
		// Create a dummy concrete ClassNode for testing abstract class
		class ConcreteClassNode extends ClassNode<ConcreteClassNode>{
			public ConcreteClassNode(PhysicalNode<ConcreteClassNode> p){
				super(p);
			}
			public ConcreteClassNode(File f){
				super(new ClassPhysicalNode<ConcreteClassNode>(f){});
			}
		}

		ConcreteClassNode node=new ConcreteClassNode(file);

		Assertions.assertNotNull(node.getPhysicalRepresentation(), "Physical representation should not be null");
		Assertions.assertNotNull(node.getModel(), "Model should not be null");
		Assertions.assertTrue(node.getPhysicalRepresentation() instanceof ClassNode.ClassPhysicalNode, "Physical rep should be ClassPhysicalNode");
		Assertions.assertTrue(node.getModel() instanceof ClassNode.ClassModel, "Model should be ClassModel");
		
		Assertions.assertEquals("TestClass", ((ClassNode.ClassModel)node.getModel()).getName());
	}

	@Test
	public void testFeatureNodeStructure() {
		File file = new File(tempDir, "TestFeature.java");
		FeatureNode.FeaturePhysicalNode pn=new FeatureNode.FeaturePhysicalNode(file);
		EditableNode node = new EditableNode(file, "TestObject", "com.example", prop);
		
		Assertions.assertNotNull(node.getPhysicalRepresentation());
		Assertions.assertNotNull(node.getModel());
	}
}
