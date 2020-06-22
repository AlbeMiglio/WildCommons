/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package wild.api.math;

import java.util.Map;

public class MathExpression {

	private Map<String, Number> variables;
	private String expression;
	private int position = -1;
	private char currentChar;
	
	public static double eval(String expression) throws MathParseException {
		return eval(expression, null);
	}

	public static double eval(String expression, Map<String, Number> variables) throws MathParseException {
		try {
			return new MathExpression(expression, variables).parse();
		} catch (Throwable t) {
			throw new MathParseException("Unexpected exception", t);
		}
	}

	private MathExpression(String expression, Map<String, Number> variables) {
		this.expression = expression;
		this.variables = variables;
	}

	private void nextChar() {
		position++;
		if (position < expression.length()) {
			currentChar = expression.charAt(position);
		} else {
			currentChar = (char) -1;
		}
	}

	private boolean eat(int charToEat) {
		while (currentChar == ' ') {
			nextChar();
		}
		if (currentChar == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}

	private double parse() throws MathParseException {
		nextChar();
		double x = parseExpression();
		if (position < expression.length()) {
			throw new MathParseException("Unexpected character: " + currentChar);
		}
		return x;
	}

	// Grammar:
	// expression = term || expression `+` term || expression `-` term
	// term = factor || term `*` factor || term `/` factor
	// factor = `+` factor || `-` factor || `(` expression `)` || number || functionName factor || variableName || factor `^` factor
	private double parseExpression() throws MathParseException {
		double x = parseTerm();
		while (true) {
			if (eat('+')) {
				x += parseTerm(); // addition
			} else if (eat('-')) {
				x -= parseTerm(); // subtraction
			} else {
				return x;
			}
		}
	}

	private double parseTerm() throws MathParseException {
		double x = parseFactor();
		while (true) {
			if (eat('*')) {
				x *= parseFactor(); // multiplication
			} else if (eat('/')) {
				x /= parseFactor(); // division
			} else {
				return x;
			}
		}
	}

	private double parseFactor() throws MathParseException {
		if (eat('+')) {
			return parseFactor(); // unary plus
		}
		if (eat('-')) {
			return -parseFactor(); // unary minus
		}

		double x;
		int startPos = this.position;
		if (eat('(')) { // parentheses
			x = parseExpression();
			eat(')');
			
		} else if (currentIsNumber()) { // numbers
			while (currentIsNumber()) {
				nextChar();
			}
			x = Double.parseDouble(expression.substring(startPos, this.position));
			
		} else if (currentIsWord()) { // functions
			while (currentIsWord()) {
				nextChar();
			}
			String word = expression.substring(startPos, this.position);
			
			if (variables != null && variables.containsKey(word)) {
				x = variables.get(word).doubleValue();
			} else {
				x = parseFactor();
				if (word.equals("sqrt")) {
					x = Math.sqrt(x);
				} else if (word.equals("sin")) {
					x = Math.sin(Math.toRadians(x));
				} else if (word.equals("cos")) {
					x = Math.cos(Math.toRadians(x));
				} else if (word.equals("tan")) {
					x = Math.tan(Math.toRadians(x));
				} else {
					throw new MathParseException("Unknown function: " + word);
				}
			}
			
		} else {
			throw new MathParseException("Unexpected character: " + currentChar);
		}

		if (eat('^')) {
			x = Math.pow(x, parseFactor()); // exponentiation
		}

		return x;
	}
	
	private boolean currentIsNumber() {
		return (currentChar >= '0' && currentChar <= '9') || currentChar == '.';
	}
	
	private boolean currentIsWord() {
		return currentChar >= 'a' && currentChar <= 'z';
	}
	
	
	public static class MathParseException extends Exception {

		private static final long serialVersionUID = 1L;

		public MathParseException(String message) {
			super(message);
		}

		public MathParseException(String message, Throwable cause) {
			super(message, cause);
		}
		
	}

}
