public class NextNode extends StatementNode
{
	private VariableNode variable;
	private ForNode forNode;
	
	public NextNode(VariableNode variable)
	{
		this.variable = variable;
		this.forNode = null;
	}
	
	public void setForNode(ForNode forNode)
	{
		this.forNode = forNode;
	}
	
	public ForNode getForNode()
	{
		return this.forNode;
	}
	
	public VariableNode getVariable()
	{
		return this.variable;
	}
	
	public String toString()
	{
		return "NEXT(" + variable +")";
	}
}