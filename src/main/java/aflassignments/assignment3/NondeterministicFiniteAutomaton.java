/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Milten Plescott
 */
class NondeterministicFiniteAutomaton extends FiniteAutomaton {

	private static int stateNameCounter = 0;  // number to be appended to "q" when the next new state name is needed
	private static Map<String, String> map;  // map old state names to new state names when merging 2 automata

	private final List<String> allStates;  // defines the order of states in adjacency matrix (rows, cols)
	private String initialState;
	private final Set<String> finalStates;

	NondeterministicFiniteAutomaton(Set<String> allStates, String initialState, Set<String> finalStates,
		Set<String> inputSymbols, Set<String[]> transitions) {
		this.allStates = new ArrayList<>(allStates);
		this.inputSymbols = inputSymbols;
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.adjMatrixSize = this.allStates.size();
		initAdjMatrix(this.allStates.size());
		parseTransitions(transitions);
	}

	private NondeterministicFiniteAutomaton(List<String> allStates, String initialState, Set<String> finalStates,
		Set<String> inputSymbols, Set<String>[][] adjacencyMatrix, int adjMatrixSize) {
		this.allStates = allStates;
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.inputSymbols = inputSymbols;
		this.adjacencyMatrix = adjacencyMatrix;
		this.adjMatrixSize = adjMatrixSize;
	}

	// creates an elementary automaton that accepts a single symbol (guaranteed to be ASCII)
	public NondeterministicFiniteAutomaton(char c) {
		String input = Character.toString(c);
		String qA = getNewStateName();
		String qB = getNewStateName();
		this.inputSymbols = new HashSet<>();
		this.inputSymbols.add(input);
		this.allStates = new ArrayList<>();
		this.allStates.add(qA);
		this.allStates.add(qB);
		this.initialState = qA;
		this.finalStates = new HashSet<>();
		this.finalStates.add(qB);

		this.adjMatrixSize = this.allStates.size();
		initAdjMatrix(this.adjMatrixSize);
		this.adjacencyMatrix[0][1].add(input);  // from qA(I) to qB(F)
	}

	// creates an elementary automaton that accepts epsilon ("")
	public NondeterministicFiniteAutomaton() {
		String q = getNewStateName();
		this.inputSymbols = new HashSet<>();
		this.allStates = new ArrayList<>();
		this.allStates.add(q);
		this.initialState = q;
		this.finalStates = new HashSet<>();
		this.finalStates.add(q);
		this.adjMatrixSize = this.allStates.size();
		initAdjMatrix(this.adjMatrixSize);
	}

	private static String getNewStateName() {
		String state = "q" + stateNameCounter;
		stateNameCounter++;
		return state;
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

	NondeterministicFiniteAutomaton union(NondeterministicFiniteAutomaton nfa) {
		NondeterministicFiniteAutomaton newNfa = this.mergeAtomata(nfa, null);
		String initial = getNewStateName();
		newNfa.allStates.add(initial);
		newNfa.initialState = initial;
		newNfa.adjMatrixSize += 1;
		newNfa.adjacencyMatrix = expand(newNfa.adjacencyMatrix, 1);
		newNfa.adjacencyMatrix[newNfa.allStates.indexOf(initial)][newNfa.allStates.indexOf(map.get(this.initialState))].add("");
		newNfa.adjacencyMatrix[newNfa.allStates.indexOf(initial)][newNfa.allStates.indexOf(map.get(nfa.initialState))].add("");

		return newNfa;
	}

	// create a copy of the array and add expandSize number of columns and rows at the end
	private static Set<String>[][] expand(Set<String>[][] array, int expandSize) {
		if (expandSize < 1) {
			System.out.println("Expand size error");
			System.exit(1);
		}
		Set<String>[][] newArr = new HashSet[array.length + expandSize][array[0].length + expandSize];
		for (int row = 0; row < newArr.length; row++) {
			for (int col = 0; col < newArr[0].length; col++) {
				if (row >= array.length || col >= array[0].length) {
					newArr[row][col] = new HashSet<>();
				}
				else {
					newArr[row][col] = array[row][col];
				}
			}
		}
		return newArr;
	}

	NondeterministicFiniteAutomaton mergeAtomata(final NondeterministicFiniteAutomaton nfa, String oldInitialState) {
		// test state names duplicates
		Set<String> testDuplicates = new HashSet<>();
		testDuplicates.addAll(this.allStates);
		testDuplicates.addAll(nfa.allStates);
		if (testDuplicates.size() != (this.allStates.size() + nfa.allStates.size())) {
			System.out.println("Got state duplicates");
			System.exit(1);
		}

		// map old states to new states
		map = new HashMap<>();
		for (String oldState : this.allStates) {
			map.put(oldState, getNewStateName());
		}
		for (String oldState : nfa.allStates) {
			map.put(oldState, getNewStateName());
		}

		// all states
		List<String> allStates = new ArrayList<>();
		for (String oldState : this.allStates) {
			allStates.add(map.get(oldState));
		}
		for (String oldState : nfa.allStates) {
			allStates.add(map.get(oldState));
		}

		Set<String> inputSymbols = new HashSet<>();
		inputSymbols.addAll(this.inputSymbols);
		inputSymbols.addAll(nfa.inputSymbols);

		// final states
		Set<String> finalStates = new HashSet<>();
		for (String oldState : this.finalStates) {
			finalStates.add(map.get(oldState));
		}
		for (String oldState : nfa.finalStates) {
			finalStates.add(map.get(oldState));
		}

		if (finalStates.size() != (this.finalStates.size() + nfa.finalStates.size())) {
			System.out.println("Got state duplicates");
			System.exit(1);
		}

		int adjMatrixSize = this.adjMatrixSize + nfa.adjMatrixSize;

		// init adj matrix
		Set<String>[][] adjacencyMatrix = new HashSet[adjMatrixSize][adjMatrixSize];
		for (int i = 0; i < adjMatrixSize; i++) {
			for (int j = 0; j < adjMatrixSize; j++) {
				adjacencyMatrix[i][j] = new HashSet<>();
			}
		}

		// copy top left quadrant (this)
		for (int row = 0; row < this.adjacencyMatrix.length; row++) {
			for (int col = 0; col < this.adjacencyMatrix[0].length; col++) {
				for (String symbol : this.adjacencyMatrix[row][col]) {
					adjacencyMatrix[row][col].add(symbol);
				}
			}
		}

		// copy bottom right quadrant (nfa)
		int offset = this.adjMatrixSize;
		for (int row = 0; row < nfa.adjacencyMatrix.length; row++) {
			for (int col = 0; col < nfa.adjacencyMatrix[0].length; col++) {
				for (String symbol : nfa.adjacencyMatrix[row][col]) {
					adjacencyMatrix[row + offset][col + offset].add(symbol);
				}
			}
		}

		return new NondeterministicFiniteAutomaton(allStates, map.get(oldInitialState), finalStates, inputSymbols, adjacencyMatrix, adjMatrixSize);
	}

	NondeterministicFiniteAutomaton concatenate(NondeterministicFiniteAutomaton nfa) {
		NondeterministicFiniteAutomaton newNfa = this.mergeAtomata(nfa, this.initialState);

		for (String oldFinalState : this.finalStates) {
			newNfa.adjacencyMatrix[newNfa.allStates.indexOf(map.get(oldFinalState))][newNfa.allStates.indexOf(map.get(nfa.initialState))].add("");
		}
		for (String oldFinalState : this.finalStates) {
			newNfa.finalStates.remove(map.get(oldFinalState));
		}

		return newNfa;
	}

	static NondeterministicFiniteAutomaton iterate(NondeterministicFiniteAutomaton nfa) {
		NondeterministicFiniteAutomaton newNfa = copyAutomaton(nfa);
		String initial = getNewStateName();
		String oldInitialState = newNfa.initialState;
		newNfa.initialState = initial;
		newNfa.finalStates.add(initial);
		newNfa.allStates.add(initial);
		newNfa.adjMatrixSize += 1;
		newNfa.adjacencyMatrix = expand(newNfa.adjacencyMatrix, 1);

		for (String str : newNfa.finalStates) {
			newNfa.adjacencyMatrix[newNfa.allStates.indexOf(str)][newNfa.allStates.indexOf(oldInitialState)].add("");
		}

		return newNfa;
	}

	private static NondeterministicFiniteAutomaton copyAutomaton(final NondeterministicFiniteAutomaton nfa) {
		// map old states to new states
		Map<String, String> map = new HashMap<>();
		for (String oldState : nfa.allStates) {
			map.put(oldState, getNewStateName());
		}

		Set<String> inputSymbols = new HashSet<>(nfa.inputSymbols);
		List<String> allStates = new ArrayList<>();
		for (int i = 0; i < nfa.allStates.size(); i++) {
			allStates.add(map.get(nfa.allStates.get(i)));
		}
		String initialState = map.get(nfa.initialState);
		Set<String> finalStates = new HashSet<>();
		for (String oldState : nfa.finalStates) {
			finalStates.add(map.get(oldState));
		}

		int adjMatrixSize = nfa.adjMatrixSize;
		Set<String>[][] adjacencyMatrix = new HashSet[adjMatrixSize][adjMatrixSize];
		for (int i = 0; i < adjMatrixSize; i++) {
			for (int j = 0; j < adjMatrixSize; j++) {
				adjacencyMatrix[i][j] = new HashSet<>();
			}
		}
		for (int row = 0; row < nfa.adjacencyMatrix.length; row++) {
			for (int col = 0; col < nfa.adjacencyMatrix[0].length; col++) {
				for (String symbol : nfa.adjacencyMatrix[row][col]) {
					adjacencyMatrix[row][col].add(symbol);
				}
			}
		}

		return new NondeterministicFiniteAutomaton(allStates, initialState, finalStates, inputSymbols, adjacencyMatrix, adjMatrixSize);
	}

}
