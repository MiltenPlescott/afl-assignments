/*
 * afl-assignments: finite automata converter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Milten Plescott
 */
public class AutomataConverter {

	private static String outputFileName;

	public static void init(String[] args) {
		if (args.length < 2) {
			System.out.println("Missing arguments.");
			System.out.println("Place instructions file in: " + System.getProperty("user.dir"));
			System.exit(1);
		}
		else {
			System.out.println("Accepted argument: " + args[0]);
			System.out.println("Accepted argument: " + args[1]);
			if (args.length > 2) {
				System.out.println("Ignored arguments:");
				for (int i = 2; i < args.length; i++) {
					System.out.println("    " + args[i]);
				}
			}

			// load file
			List<String> lines = readLines(args[0]);
			outputFileName = args[1];

			// parse input file to create nondeterministic automaton
			NondeterministicFiniteAutomaton nfa = AutomataParser.parseAutomatonFile(lines);

			// convert automaton to deterministic
			DeterministicFiniteAutomaton dfa = nfa.convert();

			// save automaton to file
			writeToFile(dfa.automatonToString());

			// take input from user
			while (true) {
				String userInput = getUserInput("DFA input string:");
				if (userInput.length() > 0) {
					for (int i = 0; i < userInput.length(); i++) {
						char c = userInput.charAt(i);
						if (!dfa.inputSymbols.contains(Character.toString(c))) {
							System.out.println("User input contains characters not in the automaton alphabet.");
							System.exit(1);
						}
					}
				}
				if (dfa.isInputAccepted(userInput)) {
					System.out.println("Input string '" + userInput + "' is accepted.");
				}
				else {
					System.out.println("Input string '" + userInput + "' is NOT accepted.");
				}
			}
		}
	}

	private static List<String> readLines(String fileName) {
		List<String> lines = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);//
			) {
			String line;
			System.out.println("Reading file in " + StandardCharsets.UTF_8.toString() + " ...");
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}
		catch (IOException ex) {
			System.out.println("File " + fileName + " could not be loaded.");
			System.exit(1);
		}
		return lines;
	}

	static void writeToFile(String text) {
		try (FileOutputStream fos = new FileOutputStream(outputFileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(osw);//
			) {
			bw.write(text);
		}
		catch (IOException e) {
			System.out.println("File " + outputFileName + " could not be saved.");
			System.exit(1);
		}

	}

	private static String getUserInput(String varNameProm) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter value of variable " + varNameProm + ": ");
		return scanner.nextLine();
	}

}
