public class ForNode extends StatementNode
{
	private AssignmentNode assignment;
	private Node limit;
	private Node increment;
	private StatementNode afterNode;
	
	public ForNode(AssignmentNode assignment, Node limit, Node increment)
	{
		this.assignment = assignment;
		this.limit = limit;
		this.increment = increment;
		this.afterNode = null;
	}
	
	public void setAfterNode(StatementNode afterNode)
	{
		this.afterNode = afterNode;
	}
	
	public StatementNode getAfterNode()
	{
		return this.afterNode;
	}
	
	public AssignmentNode getAssignment()
	{
		return assignment;
	}
	
	public Node getLimit()
	{
		return limit;
	}
	
	public Node getIncrement()
	{
		return increment;
	}
	
	public String toString()
	{
		return "FOR: ASSIGNMENT(" + assignment + ") LIMIT(" + limit + ") INCREMENT(" + increment + ")";
	}
}