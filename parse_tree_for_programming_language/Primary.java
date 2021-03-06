package parse_tree_for_programming_language;

public abstract class Primary {
	void printParseTree(String indent) {
		IO.display(indent + indent.length() + " <primary>");
	}
}


/**

<assignment list> --> { <assignment> }+
<assignment> --> <id> = <E> ";"
<E> --> <term> { (+|-) <term> }
<term> --> <primary> { (*|/) <primary> }
<primary> --> <id> | <int> | <float> | <floatE> | "(" <E> ")"

**/