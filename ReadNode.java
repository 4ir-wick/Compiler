import java.util.ArrayList;

public class ReadNode extends StatementNode
{
	ArrayList<VariableNode> variables;
	
	public ReadNode(ArrayList<VariableNode> variables)
	{
		this.variables = variables;
	}
	
	public ArrayList<VariableNode> getVariables()
	{
		return this.variables;
	}
	
	public String toString()
	{
		String str = "READ(";
		int startLength = str.length();
		for(VariableNode variable : variables)
		{
			str += variable + ", ";
		}
		if(str.length() > startLength)
			str = str.substring(0, str.length() - 2);
		str += ")";
		return str;
	}
}