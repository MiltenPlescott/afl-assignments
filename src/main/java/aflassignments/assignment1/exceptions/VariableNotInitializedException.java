/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1.exceptions;

/**
 *
 * @author Milten Plescott
 */
public class VariableNotInitializedException extends InstructionException {

	public VariableNotInitializedException(int line, String varName) {
		super(line, "Attempt to use a variable '" + varName + "' before it has been initialized.");
	}

	public static void printAndExit(int line, String varName) {
		InstructionException.printAndExit(line, "Attempt to use a variable '" + varName + "' before it has been initialized.");
	}

}
