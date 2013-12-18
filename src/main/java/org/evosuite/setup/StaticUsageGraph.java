package org.evosuite.setup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticUsageGraph {

	StaticUsageGraph() {}
	
	private static final Logger logger = LoggerFactory
			.getLogger(StaticUsageGraph.class);

	private final Set<StaticFieldReadEntry> staticFieldReads = new HashSet<StaticFieldReadEntry>();
	private final Set<StaticMethodCallEntry> staticMethodCalls = new HashSet<StaticMethodCallEntry>();

	/**
	 * Returns if there is a static method call egde (INVOKESTATIC bytecode 
	 * instruction) from <owner,methodName> to <targetClass,targetField>.
	 * 
	 * @param owner
	 * @param methodName
	 * @param targetClass
	 * @param targetMethod
	 * @return
	 */
	public boolean hasStaticMethodCall(String owner, String methodName,
			String targetClass, String targetMethod) {
		StaticMethodCallEntry call = new StaticMethodCallEntry(owner,
				methodName, targetClass, targetMethod);
		return staticMethodCalls.contains(call);
	}

	/**
	 * Add a static method call (bytecode instruction INVOKESTATIC) to the graph.
	 * 
	 * @param owner
	 * @param methodName
	 * @param targetClass
	 * @param targetMethod
	 */
	public void addStaticMethodCall(String owner, String methodName,
			String targetClass, String targetMethod) {
		StaticMethodCallEntry call = new StaticMethodCallEntry(owner,
				methodName, targetClass, targetMethod);
		logger.info("Adding new static method call: " + call.toString());
		staticMethodCalls.add(call);
	}

	/**
	 * Returns if there is a static field read egde (GETSTATIC bytecode 
	 * instruction)from <owner,methodName> to <targetClass,targetField>.
	 * 
	 * @param owner
	 * @param methodName
	 * @param targetClass
	 * @param targetField
	 * @return
	 */
	public boolean hasStaticFieldRead(String owner, String methodName,
			String targetClass, String targetField) {
		StaticFieldReadEntry read = new StaticFieldReadEntry(owner, methodName,
				targetClass, targetField);
		return staticFieldReads.contains(read);
	}

	/**
	 * Add a static field read (bytecode instruction GETSTATIC) to the graph
	 * 
	 * @param owner
	 * @param methodName
	 * @param targetClass
	 * @param targetField
	 */
	public void addStaticFieldRead(String owner, String methodName,
			String targetClass, String targetField) {
		StaticFieldReadEntry read = new StaticFieldReadEntry(owner, methodName,
				targetClass, targetField);
		logger.info("Adding new static field read: " + read.toString());
		staticFieldReads.add(read);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((staticFieldReads == null) ? 0 : staticFieldReads.hashCode());
		result = prime
				* result
				+ ((staticMethodCalls == null) ? 0 : staticMethodCalls
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaticUsageGraph other = (StaticUsageGraph) obj;
		if (staticFieldReads == null) {
			if (other.staticFieldReads != null)
				return false;
		} else if (!staticFieldReads.equals(other.staticFieldReads))
			return false;
		if (staticMethodCalls == null) {
			if (other.staticMethodCalls != null)
				return false;
		} else if (!staticMethodCalls.equals(other.staticMethodCalls))
			return false;
		return true;
	}

	/**
	 * Returns the set of class names (with dots) of those classes
	 * such that there is a at least one edge and the class is the source of
	 * the edge.
	 * 
	 * @return
	 */
	public Set<String> getSourceClasses() {
		Set<String> sourceClasses = new HashSet<String>();
		for (StaticFieldReadEntry entry : staticFieldReads) {
			sourceClasses.add(entry.getSourceClass().replace("/", "."));
		}
		for (StaticMethodCallEntry entry : staticMethodCalls) {
			sourceClasses.add(entry.getSourceClass().replace("/", "."));
		}
		return sourceClasses;
	}

	/**
	 * Returns the set of class names (with dots) of those classes
	 * such that there is a at least one edge and the class is the target of
	 * the edge.
	 * 
	 * @return
	 */
	public Set<String> getTargetClasses() {
		Set<String> targetClasses = new HashSet<String>();
		for (StaticFieldReadEntry entry : staticFieldReads) {
			targetClasses.add(entry.getTargetClass().replace("/", "."));
		}
		for (StaticMethodCallEntry entry : staticMethodCalls) {
			targetClasses.add(entry.getTargetClass().replace("/", "."));
		}
		return targetClasses;
	}

	/**
	 * Returns a classname->set(fieldname) with those static fields reached by
	 * static methods (included <clinit>)
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getStaticFields() {
		Map<String, Set<String>> staticFields = new HashMap<String, Set<String>>();
		for (StaticFieldReadEntry read : this.staticFieldReads) {
			String className = read.getTargetClass().replace("/", ".");
			if (!staticFields.containsKey(className)) {
				staticFields.put(className, new HashSet<String>());
			}
			staticFields.get(className).add(read.getTargetField());
		}
		return staticFields;
	}
}