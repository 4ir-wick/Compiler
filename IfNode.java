public class IfNode extends StatementNode
{
	BooleanOperationNode booleanOperation;
	VariableNode label;
	
	public IfNode(BooleanOperationNode booleanOperation, VariableNode label)
	{
		this.booleanOperation = booleanOperation;
		this.label = label;
	}
	
	public BooleanOperationNode getBooleanOperation()
	{
		return booleanOperation;
	}
	
	public VariableNode getLabel()
	{
		return label;
	}
	
	public String toString()
	{
		return "IF " + booleanOperation + ", GOTO " + label;
	}
}