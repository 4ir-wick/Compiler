import java.util.ArrayList;

public class InputNode extends StatementNode
{
	private final Node input;
	private ArrayList<VariableNode> parameters;
	
	public InputNode(Node input, ArrayList<VariableNode> parameters)
	{
		this.input = input;
		this.parameters = parameters;
	}
	
	public Node getOutput()
	{
		return input;
	}
	
	public ArrayList<VariableNode> getParameters()
	{
		return parameters;
	}
	
	public String toString()
	{
		String str = "INPUT: Input(" + input + ") Parameters(";
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