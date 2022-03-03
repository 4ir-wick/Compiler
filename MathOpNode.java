public class MathOpNode extends Node
{
	private Token.Type operation;
	private Node left;
	private Node right;
	
	public MathOpNode(Token.Type operation, Node left, Node right)
	{
		this.operation = operation;
		this.left = left;
		this.right = right;
	}
	
	public Token.Type getOperation()
	{
		return operation;
	}
	
	public Node getLeft()
	{
		return left;
	}
	
	public Node getRight()
	{
		return right;
	}
	
	public String toString()
	{
		return "(" + left + " " + operation + " " + right + ")";
	}
}