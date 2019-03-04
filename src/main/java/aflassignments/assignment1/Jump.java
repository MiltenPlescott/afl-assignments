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

/**
 *
 * @author Milten Plescott
 */
class Jump extends Function {

	private final Boolean type;

	public Jump(Boolean type) {
		this.type = type;
	}

	@Override
	void execute(List<Identifier> args, int instructionNumber) {
		boolean makeTheJump = false;
		int jumpLine = getJumpLine(type, args, instructionNumber);

		// watch out for nullPointerException when comparing type
		if (this.type == null) {  // JUMP
			makeTheJump = true;
		}
		else if (this.type == false) {  // JUMPF
			if (!isBoolTrue(args.get(0), instructionNumber)) {
				makeTheJump = true;
			}
		}
		else {  // JUMPT
			if (isBoolTrue(args.get(0), instructionNumber)) {
				makeTheJump = true;
			}
		}
		if (makeTheJump) {
			Instruction.script.updateInstructionCounter(jumpLine);
		}
	}

	private boolean isBoolTrue(Identifier varOrConst, int instructionNumber) {
		if (varOrConst instanceof Variable) {
			Variable var = (Variable) varOrConst;
			if (Variable.isVarDefined(var.getName())) {
				return var.getValue() != 0;
			}
			VariableNotInitializedException.printAndExit(instructionNumber, var.getName());
		}
		else if (varOrConst instanceof Constant) {
			Constant constant = (Constant) varOrConst;
			return constant.value != 0;
		}
		throw new AssertionError("Should not happen.");
	}

	private int getJumpLine(Boolean type, List<Identifier> args, int instructionNumber) {
		int jumpLineIndex = type == null ? 0 : 1;  // JUMP has line as 1st arg, JUMPT/JUMPF as 2nd arg
		if (args.get(jumpLineIndex) instanceof Constant) {
			Constant constant = (Constant) args.get(jumpLineIndex);
			int jumpTo = constant.getValue();
			if (jumpTo == instructionNumber) {
				InstructionException.printAndExit(instructionNumber, "Infinite loop detected. Attempting to jump from line " + instructionNumber + " to line " + jumpTo + ".");
			}
			return jumpTo;
		}
		InstructionException.printAndExit(instructionNumber, "Jump requires a constant, not a variable.");
		return Integer.MIN_VALUE;  // unreachable, just to make NetBeans happy
	}

}
