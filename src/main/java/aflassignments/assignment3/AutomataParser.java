/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Milten Plescott
 */
final class AutomataParser {

	private AutomataParser() {
		throw new AssertionError("Suppress default constructor for noninstantiability.");
	}

	static NondeterministicFiniteAutomaton parseAutomatonFile(List<String> lines) {
		List<NondeterministicFiniteAutomaton> automata = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).length() == 0) {  // elementary epsilon automaton
				automata.add(new NondeterministicFiniteAutomaton());
			}
			else if (lines.get(i).length() == 1) {  // elementary symbol automaton
				automata.add(new NondeterministicFiniteAutomaton(lines.get(i).charAt(0)));
			}
			else {  // union, concatenation or iteration
				String[] str = lines.get(i).split(",");
				try {
					int rowA = Integer.parseInt(str[1]) - 1;
					int rowB = 0;
					if (str.length == 3) {
						rowB = Integer.parseInt(str[2]) - 1;
					}
					switch (str[0]) {
						case "U":
							automata.add(automata.get(rowA).union(automata.get(rowB)));
							break;
						case "C":
							automata.add(automata.get(rowA).concatenate(automata.get(rowB)));
							break;
						case "I":
							automata.add(NondeterministicFiniteAutomaton.iterate(automata.get(rowA)));
							break;
					}
				}
				catch (NumberFormatException e) {
					System.out.println("Could not parse row number.");
					System.exit(1);
				}
			}
		}

		return automata.get(automata.size() - 1);
	}

}
