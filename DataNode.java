import java.util.ArrayList;

public class DataNode extends StatementNode
{
	ArrayList<Node> data;
	
	public DataNode(ArrayList<Node> data)
	{
		this.data = data;
	}
	
	public ArrayList<Node> getDataList()
	{
		return this.data;
	}
	
	public String toString()
	{
		String str = "DATA(";
		int startLength = str.length();
		for(Node dataNode : data)
		{
			str += dataNode + ", ";
		}
		if(str.length() > startLength)
			str = str.substring(0, str.length() - 2);
		str += ")";
		return str;
	}
}