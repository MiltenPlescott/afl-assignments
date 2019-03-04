/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import aflassignments.assignment1.exceptions.IllegalJumpException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Milten Plescott
 */
class Script {

	private final List<String> lines;
	private final List<Instruction> instructions = new ArrayList<>();
	private int instructionCounter = 1;

	protected Script(List<String> lines) {
		this.lines = lines;
		for (int i = 0; i < lines.size(); i++) {
			this.instructions.add(InstructionParser.parseLine(lines.get(i), i + 1));
		}
	}

	protected void run() {
		while (instructionCounter <= instructions.size()) {
			instructions.get(instructionCounter - 1).execute();
			instructionCounter++;
		}

	}

	protected void updateInstructionCounter(int i) {
		if (i < 1 || i > instructions.size()) {
			IllegalJumpException.printAndExit(this.instructionCounter);
		}
		this.instructionCounter = i - 1;  // - 1 to offset the counter increment after every instruction
	}

}
