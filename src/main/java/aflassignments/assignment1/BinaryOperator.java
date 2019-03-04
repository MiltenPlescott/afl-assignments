/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import aflassignments.assignment1.exceptions.VariableNotInitializedException;
import java.util.List;
import java.util.function.IntBinaryOperator;

/**
 *
 * @author Milten Plescott
 */
class BinaryOperator extends Function {

	private final IntBinaryOperator binOp;

	public BinaryOperator(IntBinaryOperator binOp) {
		this.binOp = binOp;
	}

	// 1st, 2nd arg -> var or const
	// 3rd arg -> var, create if not exists
	@Override
	void execute(List<Identifier> args, int instructionNumber) {
		int[] ij = firstTwoArgsToInts(args.subList(0, 2), instructionNumber);
		if (isAssignmentArgValid(args.get(2))) {
			Variable var = (Variable) args.get(2);
			int result = binOp.applyAsInt(ij[0], ij[1]);
			var.setValue(result);
		}
	}

	private int[] firstTwoArgsToInts(List<Identifier> args, int instructionNumber) {
		int[] ints = new int[2];
		for (int i = 0; i < args.size(); i++) {  // check first 2 args
			if (args.get(i) instanceof Variable) {
				Variable var = (Variable) args.get(i);
				if (Variable.isVarDefined(var.getName())) {
					ints[i] = var.getValue();
				}
				else {
					VariableNotInitializedException.printAndExit(instructionNumber, var.getName());
				}
			}
			else if (args.get(i) instanceof Constant) {
				Constant constant = (Constant) args.get(i);
				ints[i] = constant.getValue();
			}
		}
		return ints;
	}

	private boolean isAssignmentArgValid(Identifier arg) {
		if (!(arg instanceof Variable)) {
			throw new AssertionError("InstructionParser's checking against Keyword's Class[] args should not make this possible.");
		}
		Variable var = (Variable) arg;
		if (!Variable.isVarDeclaredOrDefined(var.getName())) {
			throw new AssertionError("Every created Variable should belong to either declared or defined list, so this should not be possible.");
		}
		return true;
	}

}
