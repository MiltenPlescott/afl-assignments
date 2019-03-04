/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import aflassignments.assignment1.exceptions.InstructionException;
import aflassignments.assignment1.exceptions.VariableNotInitializedException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Milten Plescott
 */
class ConsoleIO extends Function {

	// read == true  -> read from console to memory
	// read == false -> write from memory to console
	private final boolean read;

	public ConsoleIO(boolean read) {
		this.read = read;
	}

	// read arg  -> var declared or defined
	// write arg -> var defined (initialized)
	@Override
	void execute(List<Identifier> args, int instructionNumber) {
		if (args.get(0) instanceof Constant) {
			InstructionException.printAndExit(instructionNumber, "Variable required. Constant not allowed here!");
		}
		Variable var = (Variable) args.get(0);
		if (this.read == true) {  // read
			if (Variable.isVarDeclaredOrDefined(var.getName())) {
				var.setValue(getUserInput(var.getName()));
			}
			else {
				throw new AssertionError("NoT PoSsIbLe!");
			}
		}
		else if (this.read == false) {  // write
			if (Variable.isVarDefined(var.getName())) {
				System.out.println("Variable " + var.getName() + ": " + var.getValue());
			}
			else {
				VariableNotInitializedException.printAndExit(instructionNumber, var.getName());
			}
		}
	}

	private int getUserInput(String varNameProm) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter value of variable " + varNameProm + ": ");
		while (true) {
			String line = scanner.nextLine();
			try {
				int n = Integer.parseInt(line);
				System.out.println("Correct user input: " + n);
				return n;
			}
			catch (NumberFormatException ex) {
				System.out.println("Input must be an integer.");
				System.out.println("Please, try again.");
			}
		}
	}

}
