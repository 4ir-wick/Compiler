import java.util.ArrayList;

public class StatementsNode extends Node
{
	private ArrayList<StatementNode> statementNodes;
	
	public StatementsNode(ArrayList<StatementNode> statementNodes)
	{
		this.statementNodes = statementNodes;
	}
	
	public ArrayList<StatementNode> getStatementNodes()
	{
		return statementNodes;
	}
	
	public String toString()
	{
		String str = "Statements:\n";
		for(Node statement : statementNodes)
		{
			str += statement + "\n";
		}
		return str;
	}
}