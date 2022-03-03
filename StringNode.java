public class StringNode extends Node
{
	private String value;
	
	public StringNode(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public String toString()
	{
		return "STRING(" + value + ")";
	}
}