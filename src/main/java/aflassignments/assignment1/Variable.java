/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Milten Plescott
 */
class Variable extends Identifier {

	private static List<Variable> declaredVariables = new ArrayList<>();
	private static List<Variable> definedVariables = new ArrayList<>();

	private final String name;
	private Integer value;

	@SuppressWarnings("LeakingThisInConstructor")
	protected Variable(String name) throws Exception {
		if (isVarDeclaredOrDefined(name)) {
			throw new Exception("Variable already exists.");
		}
		this.name = name;
		this.value = null;
		Variable.declaredVariables.add(this);
	}

	protected static boolean isVarDeclaredOrDefined(String name) {
		return (isVarDeclared(name) || isVarDefined(name));
	}

	// returns true iff a Variable with name 'name' is only declared and not defined
	protected static boolean isVarDeclared(String name) {
		for (int i = 0; i < declaredVariables.size(); i++) {
			if (name.equals(declaredVariables.get(i).name)) {
				return true;
			}
		}
		return false;
	}

	// returns true iff a Variable with name 'name' is declared and also initialized
	protected static boolean isVarDefined(String name) {
		for (int i = 0; i < definedVariables.size(); i++) {
			if (name.equals(definedVariables.get(i).name)) {
				return true;
			}
		}
		return false;
	}

	protected static Variable getVariable(String name) throws Exception {
		for (int i = 0; i < declaredVariables.size(); i++) {
			if (name.equals(declaredVariables.get(i).name)) {
				return declaredVariables.get(i);
			}
		}
		for (int i = 0; i < definedVariables.size(); i++) {
			if (name.equals(definedVariables.get(i).name)) {
				return definedVariables.get(i);
			}
		}
		throw new Exception("Variable not found.");
	}

	protected String getName() {
		return name;
	}

	protected Integer getValue() {
		return value;
	}

	protected void setValue(int value) {
		if (isVarDeclared(this.name)) {
			declaredVariables.remove(this);
			definedVariables.add(this);
		}
		this.value = value;
	}

}
