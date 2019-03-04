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

/**
 *
 * @author Milten Plescott
 */
class Assignment extends Function {

	@Override
	void execute(List<Identifier> args, int instructionNumber) {
		// 1st var, create if not exists
		// 2nd var or const
		// 1st i, 2nd j
		Integer j = null;
		if (args.get(1) instanceof Variable) {  // 2nd arg is var
			Variable var = (Variable) args.get(1);
			if (Variable.isVarDefined(var.getName())) {  // 2nd arg is defined var
				j = var.getValue();
			}
			else {  // var not defined
				VariableNotInitializedException.printAndExit(instructionNumber, var.getName());
			}
		}
		else if (args.get(1) instanceof Constant) {  // 2nd arg is constant
			Constant constant = (Constant) args.get(1);
			j = constant.getValue();
		}

		if (isAssignmentArgValid(args.get(0))) {  // 1st arg is declared or defined
			Variable var = (Variable) args.get(0);
			if (j == null) {
				throw new AssertionError("NOT POSSIBLE!");
			}
			var.setValue(j);
		}
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
