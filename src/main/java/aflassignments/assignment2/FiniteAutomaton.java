/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment2;

import java.util.Set;

/**
 *
 * @author Milten Plescott
 */
abstract class FiniteAutomaton {

	Set<String> inputSymbols;
	Set<String>[][] adjacencyMatrix;
	int adjMatrixSize;

	abstract String automatonToString();

}
