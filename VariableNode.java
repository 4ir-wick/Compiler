public class VariableNode extends StatementNode
{
	private String name;
	
	public VariableNode(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return name;
	}
}