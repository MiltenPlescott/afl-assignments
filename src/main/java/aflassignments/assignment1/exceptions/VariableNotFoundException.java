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
public class VariableNotFoundException extends InstructionException {

	public VariableNotFoundException(int line) {
		super(line, "Requested Variable, specified by its name, was not found.");
	}

	public static void printAndExit(int line) {
		InstructionException.printAndExit(line, "Requested Variable, specified by its name, was not found.");
	}

}
