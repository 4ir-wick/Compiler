public class BooleanOperationNode extends Node
{
	Node expression1;
	Node expression2;
	Token.Type operation;
	
	public BooleanOperationNode(Node expression1, Node expression2, Token.Type operation)
	{
		this.expression1 = expression1;
		this.expression2 = expression2;
		this.operation = operation;
	}
	
	public Node getExpression1()
	{
		return expression1;
	}
	
	public Node getExpression2()
	{
		return expression2;
	}
	
	public Token.Type getOperation()
	{
		return operation;
	}
	
	public String toString()
	{
		return expression1 + " " + operation + " " + expression2;
	}
}