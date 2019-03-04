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
abstract class Function {

	abstract void execute(List<Identifier> args, int instructionNumber);

}
