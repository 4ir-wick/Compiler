public class LabeledStatementNode extends StatementNode
{
	private String label;
	private StatementNode statement;
	
	public LabeledStatementNode(String label, StatementNode statement)
	{
		this.label = label;
		this.statement = statement;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public StatementNode getStatement()
	{
		return statement;
	}
	
	public String toString()
	{
		return "LABEL(" + label + "): " + statement;
	}
}