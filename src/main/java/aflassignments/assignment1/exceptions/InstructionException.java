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
public class InstructionException {

	private final String message;

	private InstructionException() {
		throw new AssertionError("Use only the constructor with String parameter.");
	}

	public static void printAndExit(String message) {
		System.out.println(message);
		System.exit(1);
	}

	public static void printAndExit(int line, String message) {
		System.out.println("Error on line " + line + ". " + message);
		System.exit(1);
	}

	// used for exceptions that aren't bound to a line number, or the line number is not known at the time of throwing
	public InstructionException(String message) {
		this.message = message;
	}

	// all exceptions from the assignment are required to also report the line number
	public InstructionException(int line, String message) {
		this.message = "Error on line " + line + ". " + message;
	}

	public String getMessage() {
		return this.message;
	}

}
