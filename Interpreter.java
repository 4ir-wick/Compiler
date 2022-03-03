import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter
{
	private StatementsNode AST;
	
	private HashMap<String, Integer> integerVariables;
	private HashMap<String, Float> floatVariables;
	private HashMap<String, String> stringVariables;
	private HashMap<String, StatementNode> labels;
	
	private ArrayList<DataNode> data;
	
	private StatementNode currentNode;
	private Stack<StatementNode> nodeStack;
	
	public Interpreter(StatementsNode AST)
	{
		this.AST = AST;
		this.integerVariables = new HashMap<String, Integer>();
		this.floatVariables = new HashMap<String, Float>();
		this.stringVariables = new HashMap<String, String>();
		this.labels = new HashMap<String, StatementNode>();
		this.data = new ArrayList<DataNode>();
		ArrayList<StatementNode> statementNodes = this.AST.getStatementNodes();
		this.currentNode = statementNodes.get(0);
		this.nodeStack = new Stack<StatementNode>();
	}
	
	public void initialize() throws Exception
	{
		this.walkLabeledStatements();
		this.walkForStatements();
		this.walkDataStatements();
		this.walkStatements();
		System.out.println("Initial:\n" + toString());
		System.out.println("Outputs:");
		interpret(currentNode);
	}
	
	private void walkLabeledStatements()
	{
		ArrayList<StatementNode> statementNodes = this.AST.getStatementNodes();
		for(int i = 0; i < statementNodes.size(); i++)
		{
			StatementNode statementNode = statementNodes.get(i);
			LabeledStatementNode labeledStatementNode = castToLabeledStatementNode(statementNode); // check for label
			if(labeledStatementNode != null)
			{
				StatementNode child = labeledStatementNode.getStatement();
				child.setNext(statementNodes.get(i+1));
				labels.put(labeledStatementNode.getLabel(), child); // add to hashmap
			}
		}
	}
	
	private LabeledStatementNode castToLabeledStatementNode(StatementNode statementNode)
	{
		if(statementNode.getClass().getName() != "LabeledStatementNode") return null;
		LabeledStatementNode labeledStatementNode = new LabeledStatementNode(null, null);
		return labeledStatementNode.getClass().cast(statementNode);
	}
	
	private void walkForStatements()
	{
		ArrayList<StatementNode> statementNodes = this.AST.getStatementNodes();
		ForNode currentForNode = null;
		int currentForNodeIndex = 0;
		for(int i = 0; i < statementNodes.size(); i++)
		{
			StatementNode statementNode = statementNodes.get(i);
			ForNode forNode = castToForNode(statementNode); // check for for
			if(forNode != null)
			{
				currentForNode = forNode;
				currentForNodeIndex = i;
			}
			else
			{
				NextNode nextNode = castToNextNode(statementNode); // check for next
				if(nextNode != null)
				{
					nextNode.setForNode(currentForNode); // set the reference to the for node
					statementNodes.remove(i);
					statementNodes.add(i, nextNode); // replace with updated next node
					if(i + 1 < statementNodes.size())
					{
						currentForNode.setAfterNode(statementNodes.get(i + 1)); // set the reference to the node after next
						statementNodes.remove(currentForNodeIndex);
						statementNodes.add(currentForNodeIndex, currentForNode); // replace with updated for node
					}
				}
			}
		}
	}
	
	private ForNode castToForNode(StatementNode statementNode)
	{
		if(statementNode.getClass().getName() != "ForNode") return null;
		ForNode forNode = new ForNode(null, null, null);
		return forNode.getClass().cast(statementNode);
	}
	
	private NextNode castToNextNode(StatementNode statementNode)
	{
		if(statementNode.getClass().getName() != "NextNode") return null;
		NextNode nextNode = new NextNode(null);
		return nextNode.getClass().cast(statementNode);
	}
	
	private void walkDataStatements()
	{
		ArrayList<StatementNode> statementNodes = this.AST.getStatementNodes();
		for(int i = 0; i < statementNodes.size(); i++)
		{
			StatementNode statementNode = statementNodes.get(i);
			DataNode dataNode = castToDataNode(statementNode); // check for data
			if(dataNode != null)
			{
				data.add(dataNode); // add data node to collection
				statementNodes.remove(i); // remove data node from AST
			}
		}
	}
	
	private DataNode castToDataNode(StatementNode statementNode)
	{
		if(statementNode.getClass().getName() != "DataNode") return null;
		DataNode dataNode = new DataNode(null);
		return dataNode.getClass().cast(statementNode);
	}
	
	private void walkStatements()
	{
		ArrayList<StatementNode> statementNodes = this.AST.getStatementNodes();
		for(int i = 0; i < statementNodes.size(); i++)
		{
			StatementNode statementNode = statementNodes.get(i);
			if(i + 1 < statementNodes.size())
			{
				StatementNode nextStatementNode = statementNodes.get(i + 1);
				statementNode.setNext(nextStatementNode); // set the next node
			}
		}
	}
	
	private void interpret(StatementNode statement) throws Exception
	{
		if(statement instanceof ReadNode) processReadNode((ReadNode)statement);
		else if(statement instanceof AssignmentNode) processAssignmentNode((AssignmentNode)statement);
		else if(statement instanceof InputNode) processInputNode((InputNode)statement);
		else if(statement instanceof PrintNode) processPrintNode((PrintNode)statement);
		else if(statement instanceof IfNode) processIfNode((IfNode)statement);
		else if(statement instanceof GosubNode) processGosubNode((GosubNode)statement);
		else if(statement instanceof ReturnNode) processReturnNode((ReturnNode)statement);
		else if(statement instanceof ForNode) processForNode((ForNode)statement);
		else if(statement instanceof NextNode) processNextNode((NextNode)statement);
		else
			currentNode = currentNode.getNext();
		if(currentNode != null)
			interpret(currentNode);
	}
	
	private void processReadNode(ReadNode readNode) throws Exception
	{
		ArrayList<VariableNode> variables = readNode.getVariables();
		DataNode dataNode = data.remove(0); // get the data node
		ArrayList<Node> dataList = dataNode.getDataList();
		while(!dataList.isEmpty())
		{
			for(VariableNode variable : variables)
			{
				Node dataVariable = dataList.remove(0); // for each entry in the data list until the data list is empty
				if(!setVariableTypeInMap(dataVariable, variable)) // set the variable in the map
					throw new Exception("inconsistent variable type in read with data"); // if failed give exception
			}
		}
		currentNode = currentNode.getNext(); // next node
	}
	
	private boolean setVariableTypeInMap(Node dataVariable, VariableNode variable)
	{
		String variableName = variable.getName();
		if(dataVariable instanceof IntegerNode)
			return integerVariables.replace(variableName, integerVariables.get(variableName), ((IntegerNode)dataVariable).getInteger());
		else if(dataVariable instanceof FloatNode)
			return floatVariables.replace(variableName, floatVariables.get(variableName), ((FloatNode)dataVariable).getFloat());
		else if(dataVariable instanceof StringNode)
			return stringVariables.replace(variableName, stringVariables.get(variableName), ((StringNode)dataVariable).getValue());
		return false;
	}
	
	private void processAssignmentNode(AssignmentNode assignmentNode) throws Exception
	{
		String variableName = assignmentNode.getVariableNode().getName();
		Node variableValue = evaluate(assignmentNode.getValue()); // evaluate the value of the assignment
		addVariableToMap(variableName, variableValue);
		currentNode = currentNode.getNext(); // next node
	}
	
	private void addVariableToMap(String name, Node value)
	{
		if(value instanceof IntegerNode)
			this.integerVariables.put(name, ((IntegerNode)value).getInteger());
		else if(value instanceof FloatNode)
			this.floatVariables.put(name, ((FloatNode)value).getFloat());
		else if(value instanceof StringNode)
			this.stringVariables.put(name, ((StringNode)value).getValue());
	}
	
	private Node evaluate(Node value) throws Exception
	{
		if(value instanceof VariableNode)
			return getVariableValue((VariableNode)value);
		else if(value instanceof FunctionNode)
			return evaluateFunction((FunctionNode)value);
		else if(value instanceof MathOpNode)
			return evaluateMathOp((MathOpNode)value);
		else
			return value; // integer, float, and string should return themselves
	}
	
	private Node getVariableValue(VariableNode variable) // check every map for a key that contains the variable name, return the corresponding value
	{
		String variableName = variable.getName();
		if(integerVariables.containsKey(variableName))
			return new IntegerNode(integerVariables.get(variableName));
		else if(floatVariables.containsKey(variableName))
			return new FloatNode(floatVariables.get(variableName));
		else
			return new StringNode(stringVariables.get(variableName));
	}
	
	private Node evaluateFunction(FunctionNode function)
	{
		String functionName = function.getFunctionName();
		if(functionName.equals("RANDOM"))
			return new IntegerNode((int)(Integer.MAX_VALUE * Math.random()));
		else if(functionName.equals("LEFT$"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			String strInput = (String) parameters.get(0);
			int numOfChars = (int) parameters.get(1);
			String strOutput = "";
			for (int i = 0; i < numOfChars; i++) // from 0 up to number of chars
				strOutput += strInput.charAt(i);
			return new StringNode(strOutput);
		}
		else if(functionName.equals("RIGHT$"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			String strInput = (String) parameters.get(0);
			int numOfChars = (int) parameters.get(1);
			String strOutput = "";
			for (int i = strInput.length() - numOfChars; i < strInput.length(); i++) // from length minus the number of chars up to the end of string
				strOutput += strInput.charAt(i);
			return new StringNode(strOutput);
		}
		else if(functionName.equals("MID$"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			String strInput = (String) parameters.get(0);
			int index1 = (int) parameters.get(1);
			int index2 = (int) parameters.get(2);
			String strOutput = "";
			for (int i = index1; i <= index2; i++) // from index1 to index2
				strOutput += strInput.charAt(i);
			return new StringNode(strOutput);
		}
		else if(functionName.equals("NUM$"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			float num = Float.valueOf(parameters.get(0).toString()); // convert the object to a string, convert the string to a float
			if(isInteger(num))
				return new StringNode(String.valueOf((int)num));
			else
				return new StringNode(String.valueOf(num));
		}
		else if(functionName.equals("VAL"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			String str = (String)parameters.get(0);
			return new IntegerNode(Integer.parseInt(str));
		}
		else if(functionName.equals("VAL%"))
		{
			ArrayList<Object> parameters = processParameters(function.getParameters());
			String str = (String)parameters.get(0);
			return new FloatNode(Float.parseFloat(str));
		}
		else
			return new IntegerNode(0); // default to a 0
	}
	
	private ArrayList<Object> processParameters(ArrayList<Node> parameters)
	{
		ArrayList<Object> newParameters = new ArrayList<Object>();
		for(Node node : parameters)
		{
			if(node instanceof IntegerNode)
				newParameters.add(((IntegerNode)node).getInteger());
			else if(node instanceof FloatNode)
				newParameters.add(((FloatNode)node).getFloat());
			else if(node instanceof StringNode)
				newParameters.add(((StringNode)node).getValue());
		}
		return newParameters;
	}
	
	private Node evaluateMathOp(MathOpNode mathOp) throws Exception
	{
		Node left = mathOp.getLeft();
		if(left instanceof MathOpNode)
			left = evaluateMathOp((MathOpNode)left); // if the left is a mathOp, evaluate again
		float leftValue = determineNodeValue(left);
		Node right = mathOp.getRight();
		if(right instanceof MathOpNode)
			right = evaluateMathOp((MathOpNode)right); // if the right is a mathOp, evaluate again
		float rightValue = determineNodeValue(right);
		float result = operate(leftValue, rightValue, mathOp.getOperation());
		if(isInteger(result))
			return new IntegerNode((int)result);
		else
			return new FloatNode(result);
	}
	
	private float determineNodeValue(Node node) throws Exception
	{
		if(node instanceof IntegerNode)
			return ((IntegerNode)node).getInteger();
		else if(node instanceof FloatNode)
			return ((FloatNode)node).getFloat();
		else
		{
			Node variableValue =  getVariableValue((VariableNode)node);
			if(variableValue instanceof IntegerNode)
				return ((IntegerNode)variableValue).getInteger();
			else if(variableValue instanceof FloatNode)
				return ((FloatNode)variableValue).getFloat();
			else
				throw new Exception("variable cannot be a string in a math operation");
		}
	}
	
	private float operate(float leftValue, float rightValue, Token.Type operation)
	{
		if(operation == Token.Type.PLUS)
			return leftValue + rightValue;
		else if(operation == Token.Type.MINUS)
			return leftValue - rightValue;
		else if(operation == Token.Type.TIMES)
			return leftValue * rightValue;
		else
			return leftValue / rightValue;
	}
	
	private boolean isInteger(float num)
	{
		int numAsInt = (int)num;
		float numMinusNumAsInt = num - numAsInt;
		if (numMinusNumAsInt > 0)
		    return false;
		return true;
	}
	
	private void processInputNode(InputNode input)
	{
		Node output = input.getOutput();
		printlnNode(output);
		Scanner in = new Scanner(System.in);
		for(VariableNode variable : input.getParameters())
		{
			Node variableValue = getVariableValue(variable);
			Node valueToSet = null;
			if(variableValue instanceof IntegerNode)
				valueToSet = new IntegerNode(in.nextInt());
			else if(variableValue instanceof FloatNode)
				valueToSet = new FloatNode(in.nextFloat());
			else if(variableValue instanceof StringNode)
				valueToSet = new VariableNode(in.nextLine());
			addVariableToMap(variable.getName(), valueToSet);
		}
		in.close();
		currentNode = currentNode.getNext(); // next node
	}
	
	private void printlnNode(Node node)
	{
		if(node instanceof VariableNode)
		{
			Node variableValue = getVariableValue((VariableNode)node);
			if(variableValue instanceof IntegerNode)
				System.out.println(((IntegerNode)variableValue).getInteger());
			else if(variableValue instanceof FloatNode)
				System.out.println(((FloatNode)variableValue).getFloat());
			else if(variableValue instanceof StringNode)
				System.out.println(((StringNode)variableValue).getValue());
		}
		else if(node instanceof FunctionNode)
			printlnNode(evaluateFunction((FunctionNode)node));
		else if(node instanceof IntegerNode)
			System.out.println(((IntegerNode)node).getInteger());
		else if(node instanceof FloatNode)
			System.out.println(((FloatNode)node).getFloat());
		else
			System.out.println(((StringNode)node).getValue());
	}
	
	private void processPrintNode(PrintNode print)
	{
		for(Node childToPrint : print.getNodes()) printlnNode(childToPrint);
		currentNode = currentNode.getNext(); // next node
	}
	
	private void processIfNode(IfNode ifNode) throws Exception
	{
		if(evaluateBooleanExpression(ifNode.getBooleanOperation()))
		{
			nodeStack.push(ifNode.getNext()); // remember where we left off
			currentNode = labels.get(ifNode.getLabel().getName()); // get label from map
		}
		else // if failed
			currentNode = currentNode.getNext(); // next node
	}
	
	private boolean evaluateBooleanExpression(BooleanOperationNode booleanOp) throws Exception
	{
		Node expression1 = booleanOp.getExpression1();
		Node expression1Value = evaluate(expression1);
		Node expression2 = booleanOp.getExpression2();
		Node expression2Value = evaluate(expression2);
		Token.Type operation = booleanOp.getOperation();
		return compareValues(expression1Value, expression2Value, operation);
	}
	
	private boolean compareValues(Node value1Node, Node value2Node, Token.Type compare) throws Exception
	{
		float value1 = determineNodeValue(value1Node);
		float value2 = determineNodeValue(value2Node);
		if(compare == Token.Type.LESSTHAN)
			return value1 < value2;
		else if(compare == Token.Type.GREATERTHAN)
			return value1 > value2;
		else if(compare == Token.Type.EQUALS)
			return value1 == value2;
		else if(compare == Token.Type.NOTEQUALS)
			return value1 != value2;
		else if(compare == Token.Type.LESSTHANEQUALS)
			return value1 <= value2;
		else if(compare == Token.Type.GREATERTHANEQUALS)
			return value1 >= value2;
		return false;
	}
	
	private void processGosubNode(GosubNode gosubNode)
	{
		nodeStack.push(gosubNode.getNext()); // remember where we left off
		currentNode = labels.get(gosubNode.getVariable().getName()); // get label from map
	}
	
	private void processReturnNode(ReturnNode returnNode)
	{
		if(!nodeStack.empty()) // only return if there is something to return
			currentNode = nodeStack.pop();
		else // otherwise
			currentNode = returnNode.getNext(); // next node
		if(currentNode.getNext() instanceof LabeledStatementNode) // if the next node would be the label we just used
			currentNode.setNext(returnNode.getNext()); // skip past the label and go after the return
	}
	
	private void processForNode(ForNode forNode) throws Exception
	{
		if(!variableInMap(forNode.getAssignment().getVariableNode().getName(), forNode.getAssignment().getValue())) // if looping variable is not in map
			addVariableToMap(forNode.getAssignment().getVariableNode().getName(), forNode.getAssignment().getValue()); // add it to the map
		else
			updateNumberVariable(forNode.getAssignment().getVariableNode(), forNode.getIncrement(), Token.Type.PLUS); // increment looping variable with addition
		if(numPastLimit(forNode.getAssignment().getVariableNode().getName(), forNode.getLimit())) // if past limit
		{
			currentNode = forNode.getAfterNode(); // leave the loop to the node after the next node
			deleteVariableInMap(forNode.getAssignment().getVariableNode().getName(), forNode.getAssignment().getValue()); // delete the looping variable
		}
		else
			currentNode = currentNode.getNext(); // next node
	}
	
	private boolean variableInMap(String variableName, Node variableValue)
	{
		if(variableValue instanceof IntegerNode)
			return integerVariables.containsKey(variableName);
		else if(variableValue instanceof FloatNode)
			return floatVariables.containsKey(variableName);
		return false;
	}
	
	private void updateNumberVariable(VariableNode variableNode, Node increment, Token.Type operation) throws Exception
	{
		String variableName = variableNode.getName();
		Node variableValue = getVariableValue(variableNode);
		Node newValue = evaluateMathOp(new MathOpNode(operation, variableValue, increment)); // evaluate the value using the evaluate math op method
		if(newValue instanceof IntegerNode)
			integerVariables.replace(variableName, ((IntegerNode)newValue).getInteger());
		else if(newValue instanceof FloatNode)
			floatVariables.replace(variableName, ((FloatNode)newValue).getFloat());
	}
	
	private boolean numPastLimit(String variableName, Node limit)
	{
		float variableValue = 0;
		float limitValue = 0;
		if(limit instanceof IntegerNode)
		{
			variableValue = integerVariables.get(variableName);
			limitValue = ((IntegerNode)limit).getInteger();
		}
		else if(limit instanceof FloatNode)
		{
			variableValue = floatVariables.get(variableName);
			limitValue = ((FloatNode)limit).getFloat();
		}
		if(variableValue > limitValue)
			return true;
		return false;
	}
	
	private void deleteVariableInMap(String variableName, Node value)
	{
		if(value instanceof IntegerNode)
			integerVariables.remove(variableName);
		else if(value instanceof FloatNode)
			floatVariables.remove(variableName);
	}
	
	private void processNextNode(NextNode nextNode)
	{
		currentNode = nextNode.getForNode(); // next node
	}
	
	public String toString()
	{
		return	"Interpreter:\n"
				+ "Integer Variables: " + this.integerVariables.toString()
				+ "\nFloat Variables: " + this.floatVariables.toString()
				+ "\nString Varaibles: " + this.stringVariables.toString()
				+ "\nLabels: " + this.labels
				+ "\nData: " + this.data;
	}
}