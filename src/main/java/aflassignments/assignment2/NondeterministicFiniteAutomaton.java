/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Milten Plescott
 */
class NondeterministicFiniteAutomaton extends FiniteAutomaton {

	private final List<String> allStates;  // defines the order of states in adjacency matrix (rows, cols)
	private final String initialState;
	private final Set<String> finalStates;

	NondeterministicFiniteAutomaton(Set<String> allStates, String initialState, Set<String> finalStates, Set<String> inputSymbols, Set<String[]> transitions) {
		this.allStates = new ArrayList<>(allStates);
		this.inputSymbols = inputSymbols;
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.adjMatrixSize = this.allStates.size();
		initAdjMatrix(this.allStates.size());
		parseTransitions(transitions);
	}

	// to be deterministic variables marked with _D
	DeterministicFiniteAutomaton convert() {
		Set<Set<String>> allStates_D = new HashSet<>();  // HashSet of TreeSets of Strings
		Queue<Set<String>> queue = new LinkedList<>();

		// transitions_D
		List<Set<String>> fromTransitions = new ArrayList<>();
		List<String> inputTransitions = new ArrayList<>();
		List<Set<String>> toTransitions = new ArrayList<>();

		// initial state
		Set<String> initialState_D = closure(this.initialState);
		allStates_D.add(initialState_D);
		queue.add(initialState_D);

		while (queue.size() > 0) {
			Set<String> currentState = queue.remove();
			for (String symbol : inputSymbols) {
				Set<String> fromCurrentStateOnSymbol = new HashSet<>();  // a multistate, where I can get from 'currentState' after consuming 'symbol'
				for (String state : currentState) {
					for (int col = 0; col < adjMatrixSize; col++) {
						if (adjacencyMatrix[allStates.indexOf(state)][col].contains(symbol)) {
							fromCurrentStateOnSymbol.add(allStates.get(col));
						}
					}
				}
				if (fromCurrentStateOnSymbol.size() > 0) {
					Set<String> newState = closure(fromCurrentStateOnSymbol);
					if (!allStates_D.contains(newState)) {
						allStates_D.add(newState);
						queue.add(newState);
					}
					// transition: FROM currentState ON symbol TO newState
					fromTransitions.add(currentState);
					inputTransitions.add(symbol);
					toTransitions.add(newState);
				}
			}
		}

		// final states
		Set<Set<String>> finalStates_D = new HashSet<>();  // HashSet of TreeSets of Strings
		for (String finalState_N : finalStates) {
			for (Set<String> multiState_D : allStates_D) {
				if (multiState_D.contains(finalState_N)) {
					finalStates_D.add(multiState_D);
				}
			}
		}

		return new DeterministicFiniteAutomaton(allStates_D, initialState_D, finalStates_D, inputSymbols, fromTransitions, inputTransitions, toTransitions);
	}

	Set<String> closure(String state) {
		Set<String> tmpSet = new HashSet<>();
		tmpSet.add(state);
		return closure(tmpSet);
	}

	Set<String> closure(Set<String> inputStates) {
		Set<String> closuredStates = new TreeSet<>();
		Queue<String> queue = new LinkedList<>();
		queue.addAll(inputStates);
		closuredStates.addAll(queue);

		while (queue.size() > 0) {
			for (int i = 0; i < queue.size(); i++) {
				String state = queue.remove();
				for (int col = 0; col < adjMatrixSize; col++) {
					if (adjacencyMatrix[allStates.indexOf(state)][col].contains("")) {
						queue.add(allStates.get(col));
					}
				}
				if (closuredStates.containsAll(queue)) {
					return closuredStates;
				}
				closuredStates.addAll(queue);
			}
		}
		return closuredStates;
	}

	@Override
	String automatonToString() {
		StringBuilder builder = new StringBuilder();
		String ls = System.lineSeparator();
		builder.append(allStates.size()).append(ls);
		builder.append(inputSymbols.size()).append(ls);

		// state symbols
		for (String symbol : allStates) {
			builder.append(symbol);
			if (initialState.equals(symbol) && finalStates.contains(symbol)) {
				builder.append(" IF");
			}
			else if (initialState.equals(symbol)) {
				builder.append(" I");
			}
			else if (finalStates.contains(symbol)) {
				builder.append(" F");
			}
			builder.append(ls);
		}

		// input symbols
		for (String symbol : inputSymbols) {
			builder.append(symbol).append(ls);
		}

		// transitions
		for (int row = 0; row < adjMatrixSize; row++) {
			for (int col = 0; col < adjMatrixSize; col++) {
				for (String input : adjacencyMatrix[row][col]) {
					String from = allStates.get(row);
					String to = allStates.get(col);
					builder.append(from).append(",").append(input).append(",").append(to).append(ls);
				}
			}
		}

		return builder.toString();
	}

	private void initAdjMatrix(int matrixSize) {
		this.adjacencyMatrix = new HashSet[matrixSize][matrixSize];
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				this.adjacencyMatrix[i][j] = new HashSet<>();
			}
		}
	}

	private void parseTransitions(Set<String[]> transitions) {
		for (String[] transition : transitions) {
			try {
				String fromStr = transition[0];
				int fromInd = this.allStates.indexOf(fromStr);
				String input = transition[1];
				if (transition[1].length() > 1) {
					System.out.println("Multicharacter symbols not supported!");
					System.exit(fromInd);
				}
				String toStr = transition[2];
				int toInd = this.allStates.indexOf(toStr);
				adjacencyMatrix[fromInd][toInd].add(input);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("error: " + Arrays.toString(transition));
			}
		}
	}

}
