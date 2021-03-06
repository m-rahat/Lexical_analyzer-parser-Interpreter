package parse_tree_for_programming_language;

import java.util.LinkedList;

public class SList {

	LinkedList<Statement> statements;

	public SList(LinkedList<Statement> statements) {
		this.statements = statements;
	}
	
	public void printParseTree(String indent) {
		IO.displayln(indent + indent.length() + " <s list>");
		for (Statement a : statements)
			a.printParseTree(indent + " ");
	}

}
