/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Milten Plescott
 */
public class Interpreter {

	public static void init(String[] args) {
		if (args.length == 0) {
			System.out.println("Missing arguments.");
			System.out.println("Place instructions file in: " + System.getProperty("user.dir"));
			System.exit(1);
		}
		else {
			System.out.println("Accepted argument: " + args[0]);
			if (args.length > 1) {
				System.out.println("Ignored arguments:");
				for (int i = 1; i < args.length; i++) {
					System.out.println("    " + args[i]);
				}
			}
			List<String> lines = readLines(args[0]);

			Script script = new Script(lines);
			Instruction.setScript(script);
			script.run();
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

}
