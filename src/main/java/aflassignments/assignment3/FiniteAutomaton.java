/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment3;

import java.util.Set;

/**
 *
 * @author Milten Plescott
 */
abstract class FiniteAutomaton {

	Set<String> inputSymbols;  // does not contain ""
	Set<String>[][] adjacencyMatrix;  // does contain "" (if applicable)
	int adjMatrixSize;

	abstract String automatonToString();

}
