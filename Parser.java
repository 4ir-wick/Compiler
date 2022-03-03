import java.util.ArrayList;

public class Parser
{
	private ArrayList<ArrayList<Token>> tokens;
	private int index;
	
	public Parser(ArrayList<ArrayList<Token>> tokens)
	{
		this.tokens = tokens;
		this.index = 0;
	}
	
	public StatementsNode parse()
	{
		return Statements();
	}
	
	private StatementsNode Statements()
	{
		ArrayList<StatementNode> statementNodes = new ArrayList<StatementNode>();
		StatementNode statementNode;
		while(index < tokens.size())
		{
			statementNode = Statement();
			if(statementNode == null) return null;
			statementNodes.add(statementNode);
			if(matchAndRemove(Token.Type.EndOfLine) == null) return null; // we need to be at the end of line token or else the statement is incomplete
			index++; // end of statement, go to next line
		}
		return new StatementsNode(statementNodes);
	}
	
	private StatementNode Statement()
	{
		LabeledStatementNode labeledStatementNode = LabeledStatement();
		if(labeledStatementNode != null) return labeledStatementNode;
		GosubNode gosubNode = GosubStatement();
		if(gosubNode != null) return gosubNode;
		ReturnNode returnNode = ReturnStatement();
		if(returnNode != null) return returnNode;
		ForNode forNode = ForStatement();
		if(forNode != null) return forNode;
		NextNode nextNode = NextStatement();
		if(nextNode != null) return nextNode;
		IfNode ifNode = IfStatement();
		if(ifNode != null) return ifNode;
		PrintNode printNode = PrintStatement();
		if(printNode != null) return printNode;
		AssignmentNode assignmentNode = Assignment();
		if(assignmentNode != null) return assignmentNode;
		ReadNode readNode = Read();
		if(readNode != null) return readNode;
		DataNode dataNode = Data();
		if(dataNode != null) return dataNode;
		InputNode inputNode = Input();
		if(inputNode != null) return inputNode;
		return null;
	}
	
	private LabeledStatementNode LabeledStatement()
	{
		Token label = matchAndRemove(Token.Type.LABEL);
		if(label == null) return null;
		StatementNode statement = Statement();
		if(statement == null) return null;
		return new LabeledStatementNode(label.getValue(), statement);
	}
	
	private GosubNode GosubStatement()
	{
		if(matchAndRemove(Token.Type.GOSUB) == null) return null;
		Node variable = Factor();
		if(variable == null) return null;
		VariableNode variableNode = CastToVariableNode(variable); // Factor() must be casted to correct type
		if(variableNode == null) return null;
		return new GosubNode(variableNode);
	}
	
	private ReturnNode ReturnStatement()
	{
		if(matchAndRemove(Token.Type.RETURN) == null) return null;
		return new ReturnNode();
	}
	
	private ForNode ForStatement()
	{
		if(matchAndRemove(Token.Type.FOR) == null) return null;
		AssignmentNode assignment = Assignment();
		if(matchAndRemove(Token.Type.TO) == null) return null;
		
		Node limit = null;
		Node factor = Factor();
		if(factor == null) return null;
		IntegerNode integerNode = CastToIntegerNode(factor); // Factor() must be casted to correct type
		if(integerNode != null) limit = integerNode;
		FloatNode floatNode = CastToFloatNode(factor); // Factor() must be casted to correct type
		if(floatNode != null) limit = floatNode;
		if(limit == null) return null;
		
		Node increment = null;
		if(matchAndRemove(Token.Type.STEP) == null) increment = new IntegerNode(1); // STEP is optional
		else
		{
			factor = Factor();
			if(factor == null) return null;
			integerNode = CastToIntegerNode(factor); // Factor() must be casted to correct type
			if(integerNode != null) increment = integerNode;
			floatNode = CastToFloatNode(factor); // Factor() must be casted to correct type
			if(floatNode != null) increment = floatNode;
			if(increment == null) return null;
		}
		
		return new ForNode(assignment, limit, increment);
	}
	
	private NextNode NextStatement()
	{
		if(matchAndRemove(Token.Type.NEXT) == null) return null;
		Node variable = Factor();
		if(variable == null) return null;
		VariableNode variableNode = CastToVariableNode(variable); // Factor() must be casted to correct type
		if(variableNode == null) return null;
		return new NextNode(variableNode);
	}
	
	private IfNode IfStatement()
	{
		if(matchAndRemove(Token.Type.IF) == null) return null;
		BooleanOperationNode booleanExpression = BooleanExpression();
		if(booleanExpression == null) return null;
		if(matchAndRemove(Token.Type.THEN) == null) return null;
		Node label = Factor();
		if(label == null) return null;
		VariableNode labelNode = CastToVariableNode(label); // Factor() must be casted to correct type
		if(labelNode == null) return null;
		return new IfNode(booleanExpression, labelNode);
	}
	
	private BooleanOperationNode BooleanExpression()
	{
		Node expression1 = Expression();
		if(expression1 == null) return null;
		Token operation = matchAndRemove(Token.Type.GREATERTHAN);
		if(operation == null) operation = matchAndRemove(Token.Type.GREATERTHANEQUALS);
		if(operation == null) operation = matchAndRemove(Token.Type.LESSTHAN);
		if(operation == null) operation = matchAndRemove(Token.Type.LESSTHANEQUALS);
		if(operation == null) operation = matchAndRemove(Token.Type.NOTEQUALS);
		if(operation == null) operation = matchAndRemove(Token.Type.EQUALS);
		if(operation == null) return null;
		Node expression2 = Expression();
		if(expression2 == null) return null;
		return new BooleanOperationNode(expression1, expression2, operation.getType());
	}
	
	private PrintNode PrintStatement()
	{
		if(matchAndRemove(Token.Type.PRINT) == null) return null;
		PrintNode print = PrintList();
		if(print == null) return null;
		return print;
	}
	
	private PrintNode PrintList()
	{
		ArrayList<Node> nodes = new ArrayList<Node>();
		do // will always check the first value before the comma
		{
			Node listItem = Expression();
			if(listItem == null) listItem = String();
			if(listItem == null) return null;
			nodes.add(listItem);
		}while(matchAndRemove(Token.Type.COMMA) != null); // if next token is a comma, attempt to add the next expression
		return new PrintNode(nodes);
	}
	
	private StringNode String()
	{
		Token stringToken = matchAndRemove(Token.Type.STRING);
		if(stringToken == null) return null;
		return new StringNode(stringToken.getValue());
	}
	
	private AssignmentNode Assignment()
	{
		Node variable = Factor();
		if(variable == null) return null;
		VariableNode variableNode = CastToVariableNode(variable); // Factor() must be casted to correct type
		if(variableNode == null) return null;
		if(matchAndRemove(Token.Type.EQUALS) == null) return null;
		Node expression = Expression();
		if(expression == null) expression = String();
		if(expression == null) return null;
		return new AssignmentNode(variableNode, expression);
	}
	
	private Node Expression()
	{
		Node term1 = Term();
		if(term1 != null)
		{
			Token operationToken = matchAndRemove(Token.Type.PLUS); // check for plus
			if(operationToken == null) operationToken = matchAndRemove(Token.Type.MINUS); // if not plus, check for minus
			if(operationToken == null) return term1; // no more tokens, end of parse
			Node term2 = Expression(); // call self again to check for repeats
			if(term2 == null) return null;
			if(CastToFunctionNode(term2) != null) return null; // term2 should not be a function
			return new MathOpNode(operationToken.getType(), term1, term2);
		}
		FunctionNode function = FunctionInvocation();
		if(function == null) return null;
		return function;
	}
	
	private Node Term()
	{
		Node factor1 = Factor();
		if(factor1 == null) return null;
		Token operationToken = matchAndRemove(Token.Type.TIMES); // check for times
		if(operationToken == null) operationToken = matchAndRemove(Token.Type.DIVIDE); // if not times, check for divide
		if(operationToken == null) return factor1; // no times or divide, send back up and check for plus or minus
		Node factor2 = Term(); // call self again to check for repeats
		if(factor2 == null) return null;
		return new MathOpNode(operationToken.getType(), factor1, factor2);
	}
	
	private Node Factor()
	{
		Token token = matchAndRemove(Token.Type.IDENTIFIER);
		if(token != null) return new VariableNode(token.getValue());
		token = matchAndRemove(Token.Type.NUMBER);
		if(token != null)
		{
			if(token.getValue().contains(".")) // "." = float
				return new FloatNode(Float.parseFloat(token.getValue()));
			else
				return new IntegerNode(Integer.parseInt(token.getValue()));
		} // if not number, check for the parenthesis case
		if(matchAndRemove(Token.Type.LPAREN) == null) return null;
		Node expression = Expression(); // start a new expression in the parenthesis
		if(expression == null) return null;
		if(matchAndRemove(Token.Type.RPAREN) == null) return null;
		return expression; // completed parenthesis, send back up and check for times or divide
	}
	
	private FunctionNode FunctionInvocation()
	{
		Token function = matchAndRemove(Token.Type.FUNCTIONNAME);
		if(function == null) return null;
		if(matchAndRemove(Token.Type.LPAREN) == null) return null;
		ArrayList<Node> parameters = FunctionParametersList(); // parameters will never be null
		if(matchAndRemove(Token.Type.RPAREN) == null) return null;
		return new FunctionNode(function.getValue(), parameters);
	}
	
	private ArrayList<Node> FunctionParametersList()
	{
		ArrayList<Node> parameters = new ArrayList<Node>();
		do // will always check the first value before the comma
		{
			Node parameter = Expression();
			if(parameter == null) parameter = String();
			if(parameter != null) // any other option should NOT return null
				parameters.add(parameter);
		}while(matchAndRemove(Token.Type.COMMA) != null); // if next token is a comma, attempt to add the next expression
		return parameters;
	}
	
	private VariableNode CastToVariableNode(Node variable)
	{
		if(variable.getClass().getName() != "VariableNode") return null;
		VariableNode variableNode = new VariableNode(""); // create an empty variable node
		variableNode = variableNode.getClass().cast(variable); // cast the data of the input variable node to an actual variable node
		return variableNode;
	}
	
	private IntegerNode CastToIntegerNode(Node integer)
	{
		if(integer.getClass().getName() != "IntegerNode") return null;
		IntegerNode integerNode = new IntegerNode(0); // create an empty integer node
		integerNode = integerNode.getClass().cast(integer); // cast the data of the input integer node to an actual integer node
		return integerNode;
	}
	
	private FloatNode CastToFloatNode(Node floa)
	{
		if(floa.getClass().getName() != "FloatNode") return null;
		FloatNode floatNode = new FloatNode(0); // create an empty float node
		floatNode = floatNode.getClass().cast(floa); // cast the data of the input float node to an actual float node
		return floatNode;
	}
	
	private FunctionNode CastToFunctionNode(Node function)
	{
		if(function.getClass().getName() != "FunctionNode") return null;
		FunctionNode functionNode = new FunctionNode("", new ArrayList<Node>()); // create an empty function node
		functionNode = functionNode.getClass().cast(function); // cast the data of the input function node to an actual function node
		return functionNode;
	}
	
	private ReadNode Read()
	{
		if(matchAndRemove(Token.Type.READ) == null) return null;
		ReadNode read = ReadList();
		if(read == null) return null;
		return read;
	}
	
	private ReadNode ReadList()
	{
		ArrayList<VariableNode> variables = new ArrayList<VariableNode>();
		do // will always check the first value before the comma
		{
			Node variable = Factor();
			if(variable == null) return null;
			VariableNode variableNode = CastToVariableNode(variable); // Factor() must be casted to correct type
			if(variableNode == null) return null;
			variables.add(variableNode);
		}while(matchAndRemove(Token.Type.COMMA) != null); // if next token is a comma, attempt to add the next expression
		return new ReadNode(variables);
	}
	
	private DataNode Data()
	{
		if(matchAndRemove(Token.Type.DATA) == null) return null;
		DataNode data = DataList();
		if(data == null) return null;
		return data;
	}
	
	private DataNode DataList()
	{
		ArrayList<Node> nodes = new ArrayList<Node>();
		do // will always check the first value before the comma
		{
			Node node = null;
			StringNode stringNode = String();
			if(stringNode != null) node = stringNode;
			Node factor = Factor();
			if(factor != null) // if factor, check for integer and float
			{
				IntegerNode integerNode = CastToIntegerNode(factor); // Factor() must be casted to correct type
				if(integerNode != null) node = integerNode;
				FloatNode floatNode = CastToFloatNode(factor); // Factor() must be casted to correct type
				if(floatNode != null) node = floatNode;
			}
			if(node == null) return null;
			nodes.add(node);
		}while(matchAndRemove(Token.Type.COMMA) != null); // if next token is a comma, attempt to add the next expression
		return new DataNode(nodes);
	}
	
	private InputNode Input()
	{
		if(matchAndRemove(Token.Type.INPUT) == null) return null;
		Node input = null;
		StringNode stringNode = String();
		if(stringNode != null) input = stringNode;
		Node factor = Factor();
		if(factor != null) // if factor, check for variable
		{
			VariableNode variableNode = CastToVariableNode(factor); // Factor() must be casted to correct type
			if(variableNode != null) input = variableNode;
		}
		if(input == null) return null;
		if(matchAndRemove(Token.Type.COMMA) == null) return null;
		ArrayList<VariableNode> parameters = ParameterList();
		if(parameters == null) return null;
		return new InputNode(input, parameters);
	}
	
	private ArrayList<VariableNode> ParameterList()
	{
		ArrayList<VariableNode> parameters = new ArrayList<VariableNode>();
		do // will always check the first value before the comma
		{
			Node variable = Factor();
			if(variable == null) return null;
			VariableNode variableNode = CastToVariableNode(variable); // Factor() must be casted to correct type
			if(variableNode == null) return null;
			parameters.add(variableNode);
		}while(matchAndRemove(Token.Type.COMMA) != null); // if next token is a comma, attempt to add the next expression
		return parameters;
	}
	
	private Token matchAndRemove(Token.Type tokenType)
	{
		if(tokens.get(index).get(0).getType() == tokenType)
			return tokens.get(index).remove(0);
		return null;
	}
}