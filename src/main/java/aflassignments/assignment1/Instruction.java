/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import java.util.List;

/**
 *
 * @author Milten Plescott
 */
class Instruction {

	protected static Script script;

	private final int instructionNumber;
	private Function fnc;
	private List<Identifier> fncArgs;

	public Instruction(int instructionNumber) {
		this.instructionNumber = instructionNumber;
	}

	protected static void setScript(Script script) {
		Instruction.script = script;
	}

	protected void setFunction(Function fnc) {
		this.fnc = fnc;
	}

	protected void setFncArgs(List<Identifier> fncArgs) {
		this.fncArgs = fncArgs;
	}

	public void execute() {
		fnc.execute(fncArgs, instructionNumber);
	}

}
