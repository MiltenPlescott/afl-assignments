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
public class IllegalJumpException extends InstructionException {

	public IllegalJumpException(int line) {
		super(line, "Attempt to jump to a nonexistent line.");
	}

	public static void printAndExit(int line) {
		InstructionException.printAndExit(line, "Attempt to jump to a nonexistent line.");
	}

}
