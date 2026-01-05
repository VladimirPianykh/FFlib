package com.bpa4j.util.codegen;

/**
 * A representation of element in the project.
 * A model, synchronized with the file system (or other persistency mechanism).
 * </p>
 * Must at least two constructors:
 * one accepting {@link PhysicalNode},
 * and one creating the physical representation itself.
 * {@code ProjectNode} is expected to have methods with pair delegations:
 * to model and to physical node.
 * 
 * @apiNote
 * The first delegation must ALWAYS be done to the model,
 * otherwise the data may be overwriten before checks.
 */
public interface ProjectNode<T extends ProjectNode<T>>{
	/**
	 * Acts as a container for the node state.
	 * Should not modify the or store the node itself:
	 * instead, it writes to or reads the node from file,
	 * and then makes any necessary changes independendly.
	 */
	static interface PhysicalNode<V extends ProjectNode<V>>{
		/**
		 * Deletes the physical representation.
		 */
		void clear();
		/**
		 * Creates a new node physical representation.
		 * It may be a file, or a database record or any other persistency mechanism.
		 */
		void persist(NodeModel<V>node);
		/**
		 * Loads the node from this container.
		 * @return
		 */
		NodeModel<V>load();
		/**
		 * Checks whether this container is "full"
		 * (there is a real physical representation).
		 */
		boolean exists();
	}
	/**
	 * Stores the node's data.
	 * Has only validation logic.
	 */
	static interface NodeModel<V extends ProjectNode<V>>{
		
	}
	PhysicalNode<T>getPhysicalRepresentation();
	NodeModel<T>getModel();
}