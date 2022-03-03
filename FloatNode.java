public class FloatNode extends Node
{
	private float num;
	
	public FloatNode(float num)
	{
		this.num = num;
	}
	
	public float getFloat()
	{
		return num;
	}
	
	public String toString()
	{
		return Float.toString(num);
	}
}