/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

/**
 *
 * @author Milten Plescott
 */
class Constant extends Identifier {

	protected final int value;

	protected Constant(int value) {
		this.value = value;
	}

	protected int getValue() {
		return value;
	}

}
