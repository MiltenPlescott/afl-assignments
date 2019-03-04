/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import aflassignments.assignment1.exceptions.InstructionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Milten Plescott
 */
final class InstructionParser {

	private InstructionParser() {
		throw new AssertionError("Suppress default constructor for noninstantiability.");
	}

	protected static Instruction parseLine(String line, int lineNumber) {
		Instruction instruction = new Instruction(lineNumber);
		String[] instructionSegments = line.split(",");
		Keyword keyword = parseKeyword(instructionSegments[0], lineNumber);
		List<Identifier> args = parseArgs(Arrays.asList(instructionSegments).subList(1, instructionSegments.length), keyword, lineNumber);
		Function fnc = keyword.getFnc();
		instruction.setFunction(fnc);
		instruction.setFncArgs(args);
		return instruction;
	}

	/**
	 * Compares {@code str} with all known keywords and returns the matching keyword.
	 *
	 * @param firstLineSegment substring of the line from the script before the first delimiter (comma)
	 * @param lineNumber number of the line in script (indexing from 1) that begins with {@code strs}
	 * @return matching {@link Keyword}
	 */
	private static Keyword parseKeyword(String firstLineSegment, int lineNumber) {
		Keyword keyword = Keyword.getKeyword(firstLineSegment);
		if (keyword == null) {
			InstructionException.printAndExit(lineNumber, "Instruction keyword/symbol has illegal name.");
		}
		return keyword;
	}

	private static List<Identifier> parseArgs(List<String> segments, Keyword keyword, int lineNumber) {
		if (segments.size() < keyword.argCount()) {
			InstructionException.printAndExit(lineNumber, "Instruction '" + keyword.getSymbol() + "' requires additional arguments.");
		}
		if (segments.size() > keyword.argCount()) {
			InstructionException.printAndExit(lineNumber, "Instruction '" + keyword.getSymbol() + "' has too many arguments.");
		}
		else {  // right number of arguments
			List<Identifier> args = new ArrayList<>();
			for (String segment : segments) {
				if (isValidVariable(segment)) {
					Variable var;
					try {  // try to create Variable
						var = new Variable(segment);
						args.add(var);
					}
					catch (Exception ex) {  // variable with that name already exists
						try {  // get variable if already exists
							var = Variable.getVariable(segment);
							args.add(var);
						}
						catch (Exception ex1) { // variable already exists but not found in declared or defined variables
							throw new AssertionError("Catch-22: this shouldn't have happened (o_o)." + System.lineSeparator()
								+ "If it does, at first light, on the fifth day, look for the bug in the Variable class. At dawn, the bug will be fixed.");
						}
					}
				}
				else if (isValidConstant(segment)) {
					try {
						int n = Integer.parseInt(segment);
						args.add(new Constant(n));
					}
					catch (NumberFormatException ex) {
						InstructionException.printAndExit(lineNumber, "'" + segment + "' cannot be parsed as an integer.");
					}
				}
				else {
					InstructionException.printAndExit(lineNumber, "Identifier has an illegal name.");
				}
			}
			if (keyword.isEveryArgValid(args)) {
				return args;
			}
			else {
				InstructionException.printAndExit(lineNumber, "Expected arguments type mismatch.");
			}
		}
		return null; // unreachable statement, just to make NetBeans happy
	}

	/**
	 * Checks if {@code str} starts with a letter.
	 *
	 * @param str
	 * @return true iff {@code str} is a valid variable, false otherwise
	 */
	private static boolean isValidVariable(String str) {
		return Character.isLetter(str.charAt(0));
	}

	/**
	 * Checks if {@code str} contains only digits.
	 *
	 * @param str
	 * @return true iff {@code str} is a valid constant, false otherwise
	 */
	private static boolean isValidConstant(String str) {
//		for (char c : str.toCharArray()) {
		for (int i = 0; i < str.length(); i++) {
			if (i == 0) {
				if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '-') {
					return false;
				}
			}
			else {
				if (!Character.isDigit(str.charAt(i))) { // Character.isDigit return false for decimal point (, or .) or minus (-)
					return false;
				}
			}
		}
		return true;
	}

}
