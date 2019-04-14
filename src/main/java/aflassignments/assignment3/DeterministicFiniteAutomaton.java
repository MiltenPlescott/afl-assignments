/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Milten Plescott
 */
class DeterministicFiniteAutomaton extends FiniteAutomaton {

	private final List<Set<String>> allStates;  // List of TreeSets of Strings -- determines the order of states (rows and columns) in adjecencyMatrix
	private final Set<String> initialState;
	private final Set<Set<String>> finalStates;  // HashSet of TreeSets

	DeterministicFiniteAutomaton(Set<Set<String>> allStates, Set<String> initialState, Set<Set<String>> finalStates, Set<String> inputSymbols,
		List<Set<String>> fromTransitions, List<String> inputTransitions, List<Set<String>> toTransitions) {
		this.allStates = new ArrayList<>(allStates);
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.inputSymbols = inputSymbols;

		for (String symbol : inputSymbols) {
			if (symbol.length() != 1) {
				System.out.println("Deterministic finite automaton requires single character input symbols!");
				System.exit(1);
			}
		}

		if (fromTransitions.size() != inputTransitions.size() || inputTransitions.size() != toTransitions.size()) {
			System.out.println("These transition lists should have same size.");
			System.exit(1);
		}

		// transitions: 3 vectors to matrix
		this.adjMatrixSize = allStates.size();
		this.adjacencyMatrix = new HashSet[this.adjMatrixSize][this.adjMatrixSize];
		for (int i = 0; i < fromTransitions.size(); i++) {
			int row = this.allStates.indexOf(fromTransitions.get(i));
			int col = this.allStates.indexOf(toTransitions.get(i));
			if (this.adjacencyMatrix[row][col] == null) {
				this.adjacencyMatrix[row][col] = new HashSet<>();
			}
			this.adjacencyMatrix[row][col].add(inputTransitions.get(i));
		}

		verifyDeterminismOfAdjecencyMatrix();
	}

	// make sure there are no epsilon ("") transitions and that from each state there is no more than one transition on a given input symbol
	void verifyDeterminismOfAdjecencyMatrix() {
		for (int row = 0; row < adjMatrixSize; row++) {
			List<String> list = new ArrayList<>();
			for (int col = 0; col < adjMatrixSize; col++) {
				if (adjacencyMatrix[row][col] != null) {
					list.addAll(adjacencyMatrix[row][col]);
				}
			}
			Set<String> set = new HashSet<>(list);
			if (list.contains("") || list.size() != set.size()) {
				System.out.println("Not a deterministic finite automaton!");
				System.exit(1);
			}
		}
	}

	boolean isInputAccepted(String word) {
		Set<String> currentState = new TreeSet<>();
		currentState.addAll(initialState);
		if (word.length() == 0 && isInitialFinal()) {  // accept empty input
			return true;
		}
		else if (finalStates.isEmpty()) {
			System.out.println("Infinite loop detected.");
			System.exit(1);
		}
		else {
			for (int i = 0; i < word.length(); i++) {
				currentState = moveToState(currentState, word.substring(i, i + 1));
				if (currentState == null) {
					return false;
				}
			}
			if (isStateFinal(currentState)) {
				return true;
			}
			else {
				return false;
			}
		}

		return false;  // to make NB happy
	}

	@Override
	String automatonToString() {
		StringBuilder builder = new StringBuilder();
		String ls = System.lineSeparator();
		builder.append(allStates.size()).append(ls);
		builder.append(inputSymbols.size()).append(ls);

		// state symbols
		for (Set<String> state : allStates) {
			builder.append(multiStateToString(state));

			if (multiStateToString(initialState).equals(multiStateToString(state)) && isStateFinal(state)) {
				builder.append(" IF");
			}
			else if (multiStateToString(initialState).equals(multiStateToString(state))) {
				builder.append(" I");
			}
			else if (isStateFinal(state)) {
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
				if (adjacencyMatrix[row][col] != null) {

					for (String input : adjacencyMatrix[row][col]) {
						String from = multiStateToString(allStates.get(row));
						String to = multiStateToString(allStates.get(col));
						builder.append(from).append(",").append(input).append(",").append(to).append(ls);
					}
				}
			}
		}

		return builder.toString();
	}

	String multiStateToString(Set<String> multiState) {
		StringBuilder builder = new StringBuilder();
		for (String str : multiState) {
			builder.append(str);
		}
		return builder.toString();
	}

	boolean isStateFinal(Set<String> state) {
		for (Set<String> fs : this.finalStates) {
			if (state.equals(fs)) {
				return true;
			}
		}
		return false;
	}

	boolean isInitialFinal() {
		for (Set<String> state : allStates) {
			if (multiStateToString(initialState).equals(multiStateToString(state)) && isStateFinal(state)) {
				return true;
			}
		}
		return false;
	}

	// returns null, if there is no transition from 'fromState' on 'consumed' input
	Set<String> moveToState(Set<String> fromState, String consumed) {
		Set<String> toState = new TreeSet<>();
		List<String> duplicates = new ArrayList<>();

		Set<String>[] matrixRow = adjacencyMatrix[allStates.indexOf(fromState)];
		for (int i = 0; i < matrixRow.length; i++) {
			if (matrixRow[i] != null) {
				duplicates.addAll(matrixRow[i]);

				if (matrixRow[i].contains(consumed)) {
					toState = allStates.get(i);
				}
			}
		}

		if (toState == null) {
			System.out.println("Null state error.");
			System.exit(1);
		}

		if (toState.isEmpty()) {
			return null;
		}

		int countSameAsConsumed = 0;
		for (String str : duplicates) {
			if (consumed.equals(str)) {
				countSameAsConsumed++;
			}
		}
		if (countSameAsConsumed > 1) {
			System.out.println("Not really deterninistic, is it? State: " + fromState + " can get to "
				+ countSameAsConsumed + " different states after consuming " + consumed + ".");
			System.exit(1);
		}

		return toState;
	}

}
