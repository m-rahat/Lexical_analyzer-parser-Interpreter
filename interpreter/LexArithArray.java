package interpreter;
/**

This class is a lexical analyzer for the 26 token categories <id> through <comma> defined by:

<letter> --> a | b | ... | z | A | B | ... | Z
<digit> --> 0 | 1 | ... | 9
<id> --> <letter> { <letter> | <digit> }
<int> --> {<digit>}+
<float> --> {<digit>}+ "." {<digit>}+
<floatE> --> <float> (e|E) [+|-] {<digit>}+
<add> --> +
<sub> --> -
<mul> --> *
<div> --> /
<or> --> "||"
<and> --> "&&"
<inv> --> !
<lt> --> "<"
<le> --> "<="
<gt> --> ">"
<ge> --> ">="
<eq> --> "=="
<neq> --> "!="
<assign> --> = 
<LParen> --> (
<RParen> --> )
<LBrace> --> {
<RBrace> --> }
<LBracket> --> [
<RBracket> --> ]
<semicolon> --> ;
<comma> --> ,

This class implements a DFA that will accept the above tokens.
The DFA has 26 final and 6 non-final states represented by enum-type literals.

There are also 6 special states for the keywords:

if, else, while, returnVal, new, print

The keywords are initially extracted as identifiers.
The keywordCheck() function checks if the extracted token is a keyword,
and if so, moves the DFA to the corresponding special state.

The states are represented by an Enum type called "State".
The function "driver" is the driver to operate the DFA. 
The array "nextState" returns the next state given the current state and the input character.

To modify this lexical analyzer to recognize a different token set,
the enum type "State", the array "nextState", the functions "setNextState", "setKeywordMap" need to be modified.
The function "driver" and the other utility functions remain the same.

**/

import java.util.*;

public abstract class LexArithArray extends IO {
	public enum State {
		// final states ordinal number token accepted

		Add, // 0 +
		Sub, // 1 -
		Mul, // 2 *
		Div, // 3 /
		Or, // 4 ||
		And, // 5 &&
		Inv, // 6 !
		Lt, // 7 <
		Le, // 8 <=
		Gt, // 9 >
		Ge, // 10 >=
		Eq, // 11 ==
		Neq, // 12 !=
		Assign, // 13 =
		Id, // 14 identifiers
		Int, // 15 integers
		Float, // 16 floats without exponentiation part
		FloatE, // 17 floats with exponentiation part
		LParen, // 18 (
		RParen, // 19 )
		LBrace, // 20 {
		RBrace, // 21 }
		LBracket, // 22 [
		RBracket, // 23 ]
		Semicolon, // 24 ;
		Comma, // 25 ,

		// non-final states string recognized

		Start, // 26 the empty string
		Bar, // 27 |
		Ampersand, // 28 &
		Period, // 29 "."
		E, // 30 float parts ending with E or e
		EPlusMinus, // 31 float parts ending with + or - in exponentiation par

		// keyword states

		Keyword_if, Keyword_else, Keyword_while, Keyword_returnVal, Keyword_new, Keyword_print,

		UNDEF;

		// By enumerating the final states first and then the non-final states,
		// test for a final state is done by testing if the state's ordinal number
		// is less than or equal to that of Comma.

		boolean isFinal() {
			return this.compareTo(Comma) <= 0;
		}

		boolean isArithOp() {
			return this.compareTo(Div) <= 0;
		}

		boolean isBoolOp() {
			return this.compareTo(Or) >= 0 && this.compareTo(Inv) <= 0;
		}

		boolean isCompOp() {
			return this.compareTo(Lt) >= 0 && this.compareTo(Neq) <= 0;
		}
	}

	public static String t; // holds an extracted token
	public static State state; // the current state of the DFA

	private static State nextState[][] = new State[32][128];

	// This array implements the state transition function
	// State x (ASCII char set) --> State.
	// The state argument is converted to its ordinal number used as
	// the first array index from 0 through 31.

	private static HashMap<String, State> keywordMap = new HashMap<String, State>();

	private static void setKeywordMap() {
		keywordMap.put("if", State.Keyword_if);
		keywordMap.put("else", State.Keyword_else);
		keywordMap.put("while", State.Keyword_while);
		keywordMap.put("returnVal", State.Keyword_returnVal);
		keywordMap.put("new", State.Keyword_new);
		keywordMap.put("print", State.Keyword_print);
	}

	private static int driver() {
		State nextSt; // the next state of the DFA

		t = "";
		state = State.Start;

		if (Character.isWhitespace((char) a))
			a = getChar(); // get the next non-whitespace character
		if (a == -1) // end-of-stream is reached
			return -1;

		while (a != -1) // do the body if "a" is not end-of-stream
		{
			c = (char) a;
			nextSt = nextState[state.ordinal()][a];
			if (nextSt == State.UNDEF) // The DFA will halt.
			{
				if (state.isFinal())
					return 1; // valid token extracted
				else // "c" is an unexpected character
				{
					t = t + c;
					a = getNextChar();
					return 0; // invalid token found
				}
			} else // The DFA will go on.
			{
				state = nextSt;
				t = t + c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if (state.isFinal())
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	private static void setNextState() {
		for (int s = 0; s <= 31; s++)
			for (int c = 0; c <= 127; c++)
				nextState[s][c] = State.UNDEF;

		for (char c = 'A'; c <= 'Z'; c++) {
			nextState[State.Start.ordinal()][c] = State.Id;
			nextState[State.Id.ordinal()][c] = State.Id;
		}

		for (char c = 'a'; c <= 'z'; c++) {
			nextState[State.Start.ordinal()][c] = State.Id;
			nextState[State.Id.ordinal()][c] = State.Id;
		}

		for (char d = '0'; d <= '9'; d++) {
			nextState[State.Start.ordinal()][d] = State.Int;
			nextState[State.Int.ordinal()][d] = State.Int;
			nextState[State.Id.ordinal()][d] = State.Id;
			nextState[State.Period.ordinal()][d] = State.Float;
			nextState[State.Float.ordinal()][d] = State.Float;
			nextState[State.E.ordinal()][d] = State.FloatE;
			nextState[State.EPlusMinus.ordinal()][d] = State.FloatE;
			nextState[State.FloatE.ordinal()][d] = State.FloatE;
		}

		nextState[State.Int.ordinal()]['.'] = State.Period;
		nextState[State.Float.ordinal()]['e'] = state.E;
		nextState[State.Float.ordinal()]['E'] = state.E;
		nextState[State.E.ordinal()]['+'] = State.EPlusMinus;
		nextState[State.E.ordinal()]['-'] = State.EPlusMinus;

		nextState[State.Start.ordinal()]['+'] = State.Add;
		nextState[State.Start.ordinal()]['-'] = State.Sub;
		nextState[State.Start.ordinal()]['*'] = State.Mul;
		nextState[State.Start.ordinal()]['/'] = State.Div;
		nextState[State.Start.ordinal()]['('] = State.LParen;
		nextState[State.Start.ordinal()][')'] = State.RParen;
		nextState[State.Start.ordinal()]['{'] = State.LBrace;
		nextState[State.Start.ordinal()]['}'] = State.RBrace;
		nextState[State.Start.ordinal()]['['] = State.LBracket;
		nextState[State.Start.ordinal()][']'] = State.RBracket;
		nextState[State.Start.ordinal()][';'] = State.Semicolon;
		nextState[State.Start.ordinal()][','] = State.Comma;
		nextState[State.Start.ordinal()]['|'] = State.Bar;
		nextState[State.Start.ordinal()]['&'] = State.Ampersand;
		nextState[State.Bar.ordinal()]['|'] = State.Or;
		nextState[State.Ampersand.ordinal()]['&'] = State.And;
		nextState[State.Start.ordinal()]['!'] = State.Inv;
		nextState[State.Start.ordinal()]['='] = State.Assign;
		nextState[State.Start.ordinal()]['<'] = State.Lt;
		nextState[State.Start.ordinal()]['>'] = State.Gt;
		nextState[State.Inv.ordinal()]['='] = State.Neq;
		nextState[State.Assign.ordinal()]['='] = State.Eq;
		nextState[State.Lt.ordinal()]['='] = State.Le;
		nextState[State.Gt.ordinal()]['='] = State.Ge;
	} // end setNextState

	private static void keywordCheck() {
		State keywordState = keywordMap.get(t);
		if (keywordState != null) // "t" has a keyword
			state = keywordState;
	}

	public static void getToken() {
		int i = driver();
		if (state == State.Id)
			keywordCheck();
		else if (i == 0)
			displayln(t + " : Lexical Error, invalid token");
	}

	public static void setLex()

	{
		setNextState();
		setKeywordMap();
	}

	public static void main(String argv[]) {
		setIO(argv[0], argv[1]);
		setLex();

		int i;

		while (a != -1) // while "a" is not end-of-stream
		{
			i = driver(); // extract the next token
			if (i == 1) {
				if (state == State.Id)
					keywordCheck();
				displayln(t + "   : " + state.toString());
			} else if (i == 0)
				displayln(t + " : Lexical Error, invalid token");
		}

		closeIO();
	}
}
