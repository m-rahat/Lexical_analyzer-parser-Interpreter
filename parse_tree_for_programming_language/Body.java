package parse_tree_for_programming_language;

public class Body {

	SList slist;

	public Body(SList slist) {
		this.slist = slist;
	}

	void printParseTree(String indent) {
		String indent1 = indent + " ";
		IO.displayln(indent + indent.length() + " <Body>");
		slist.printParseTree(indent1);
	}
	
}
