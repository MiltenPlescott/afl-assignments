/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Milten Plescott
 */
final class AutomataParser {

	private AutomataParser() {
		throw new AssertionError("Suppress default constructor for noninstantiability.");
	}

	static NondeterministicFiniteAutomaton parseAutomatonFile(List<String> lines) {
		int lineIndex = 0;
		int numStates = 0;
		int numInputSymbols = 0;

		try {
			numStates = Integer.parseInt(lines.get(lineIndex));
			lineIndex++;
			numInputSymbols = Integer.parseInt(lines.get(lineIndex));
			lineIndex++;
		}
		catch (NumberFormatException e) {
			System.out.println("Could not parse number of states/symbols.");
			System.exit(1);
		}
		if (numStates < 1 || numInputSymbols < 1) {
			throw new AssertionError("Illegal number of states and/or symbols.");
		}

		Set<String> allStates = new HashSet<>();
		String initialState = null;
		Set<String> finalStates = new HashSet<>();
		int forLimit = lineIndex + numStates;
		for (; lineIndex < forLimit; lineIndex++) {
			String[] strArr = lines.get(lineIndex).split(" ");
			allStates.add(strArr[0]);
			if (strArr.length > 1) {
				switch (strArr[1]) {
					case "I":
						initialState = strArr[0];
						break;
					case "F":
						finalStates.add(strArr[0]);
						break;
					case "IF":
						finalStates.add(strArr[0]);
						initialState = strArr[0];
						break;
				}
			}
		}

		Set<String> inputSymbols = new HashSet<>();
		forLimit = lineIndex + numInputSymbols;
		for (; lineIndex < forLimit; lineIndex++) {
			String sym = lines.get(lineIndex);
			if (sym.length() > 1) {
				System.out.println("Multicharacter symbols not supported!");
				System.exit(1);
			}
			inputSymbols.add(sym);
		}

		Set<String[]> transitions = new HashSet<>();
		forLimit = lines.size();
		for (; lineIndex < forLimit; lineIndex++) {
			transitions.add(lines.get(lineIndex).split(","));
		}

		for (String[] str : transitions) {
			if (str[1].length() > 1) {
				System.out.println("Multicharacter symbols not supported!");
				System.exit(1);
			}
		}

		if (numStates != allStates.size()
			|| numInputSymbols != inputSymbols.size()
			|| finalStates.size() <= 0
			|| initialState == null) {
			throw new AssertionError("Automaton parsing error.");
		}

		return new NondeterministicFiniteAutomaton(allStates, initialState, finalStates, inputSymbols, transitions);
	}

}
