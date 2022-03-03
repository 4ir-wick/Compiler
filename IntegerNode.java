public class IntegerNode extends Node
{
	private int num;
	
	public IntegerNode(int num)
	{
		this.num = num;
	}
	
	public int getInteger()
	{
		return num;
	}
	
	public String toString()
	{
		return Integer.toString(num);
	}
}