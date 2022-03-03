public class AssignmentNode extends StatementNode
{
	private VariableNode variableNode;
	private Node value;
	
	public AssignmentNode(VariableNode variableNode, Node value)
	{
		this.variableNode = variableNode;
		this.value = value;
	}
	
	public VariableNode getVariableNode()
	{
		return variableNode;
	}
	
	public Node getValue()
	{
		return value;
	}
	
	public String toString()
	{
		return variableNode + " = " + value + "";
	}
}