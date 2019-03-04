/*
 * afl-assignments: interpreter
 *
 * Copyright (c) 2019, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier: MIT
 */
package aflassignments.assignment1;

import java.util.List;

/**
 *
 * @author Milten Plescott
 */
enum Keyword {

	READ("READ", 1, new Class[]{Variable.class}, new ConsoleIO(true)),
	WRITE("WRITE", 1, new Class[]{Variable.class}, new ConsoleIO(false)),
	ADD("+", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x + y))),
	SUBTRACT("-", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x - y))),
	MULTIPLY("*", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x * y))),
	LESS_THAN("<", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x < y ? 1 : 0))),
	GREATER_THAN(">", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x > y ? 1 : 0))),
	LESS_EQUAL("<=", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x <= y ? 1 : 0))),
	GREATER_EQUAL(">=", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x >= y ? 1 : 0))),
	EQUAL("==", 3, new Class[]{Identifier.class, Identifier.class, Variable.class}, new BinaryOperator((x, y) -> (x == y ? 1 : 0))),
	ASSIGN("=", 2, new Class[]{Variable.class, Identifier.class}, new Assignment()),
	JUMP("JUMP", 1, new Class[]{Constant.class}, new Jump(null)),
	JUMP_TRUE("JUMPT", 2, new Class[]{Identifier.class, Constant.class}, new Jump(true)),
	JUMP_FALSE("JUMPF", 2, new Class[]{Identifier.class, Constant.class}, new Jump(false)),
	NOP("NOP", 0, new Class[]{}, new Function() {
		@Override
		void execute(List<Identifier> list, int instructionNumber) {
			assert true;
		}
	});

	private final String symbol;
	private final int argCount;
	private final Class[] args;
	private final Function fnc;

	private Keyword(String symbol, int argCount, Class[] args, Function fnc) {
		this.symbol = symbol;
		this.argCount = argCount;
		this.args = args;
		this.fnc = fnc;
	}

	protected static boolean isKeyword(String symbol) {
		for (Keyword k : Keyword.values()) {
			if (symbol.equals(k.getSymbol())) {
				return true;
			}
		}
		return false;
	}

	protected static Keyword getKeyword(String symbol) {
		for (Keyword k : Keyword.values()) {
			if (symbol.equals(k.getSymbol())) {
				return k;
			}
		}
		return null;
	}

	protected boolean isEveryArgValid(List<Identifier> actualArgs) {
		for (int i = 0; i < actualArgs.size(); i++) {
			if (!this.args[i].isInstance(actualArgs.get(i))) {
				return false;
			}
		}
		return true;
	}

	protected String getSymbol() {
		return symbol;
	}

	protected int argCount() {
		return argCount;
	}

	protected Function getFnc() {
		return fnc;
	}

	@Override
	public String toString() {
		return symbol;
	}

}
