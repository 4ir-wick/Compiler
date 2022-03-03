import java.util.ArrayList;

public class PrintNode extends StatementNode
{
	private ArrayList<Node> nodes;
	
	public PrintNode(ArrayList<Node> nodes)
	{
		this.nodes = nodes;
	}
	
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	public String toString()
	{
		String str = "PRINT(";
		int startLength = str.length();
		for(Node node : nodes)
		{
			str += node + ", ";
		}
		if(str.length() > startLength)
			str = str.substring(0, str.length() - 2);
		str += ")";
		return str;
	}
}