public class GosubNode extends StatementNode
{
	private VariableNode variable;
	
	public GosubNode(VariableNode variable)
	{
		this.variable = variable;
	}
	
	public VariableNode getVariable()
	{
		return variable;
	}
	
	public String toString()
	{
		return "GOSUB(" + variable + ")";
	}
}