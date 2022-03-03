import java.util.ArrayList;

public class FunctionNode extends Node
{
	String functionName;
	ArrayList<Node> parameters;
	
	public FunctionNode(String functionName, ArrayList<Node> parameters)
	{
		this.functionName = functionName;
		this.parameters = parameters;
	}
	
	public String getFunctionName()
	{
		return functionName;
	}
	
	public ArrayList<Node> getParameters()
	{
		return parameters;
	}
	
	public String toString()
	{
		String str = "FUNCTION: " + functionName + "(";
		int startLength = str.length();
		for(Node parameter : parameters)
		{
			str += parameter + ", ";
		}
		if(str.length() > startLength)
			str = str.substring(0, str.length() - 2);
		str += ")";
		return str;
	}
}