import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class Basic
{
	public static void main(String[] args) throws Exception
	{
		if(args.length != 1)
		{
			throw new IllegalArgumentException("Requires filename as only argument");
		}
		String filename = args[0];
		Path path = Paths.get(filename);
		List<String> lines = Files.readAllLines(path);
		
		ArrayList<ArrayList<Token>> tokensArray = new ArrayList<ArrayList<Token>>();
		int i = 0;
		for(String line : lines)
		{
			Lexer lexer = new Lexer();
			try // test every line, will notify if a line fails
			{
				ArrayList<Token> tokens = lexer.lex(line);
				tokensArray.add(tokens);
			}
			catch(Exception e)
			{
				System.out.println("Exception caught at line " + i + "\n" + e);
			}
			i++;
		}
		
		/*
		for(ArrayList<Token> tokens : tokensArray)
		{
			for(Token token : tokens)
			{
				System.out.print(token + " ");
			}
			System.out.println();
		}
		/**/
		
		Parser parser = new Parser(tokensArray);
		StatementsNode AST = parser.parse();
		
		/*
		if(AST != null)
			System.out.println(AST);
		else
			System.out.println("Parse Failed");
		/**/
		
		Interpreter interpreter = new Interpreter(AST);
		try
		{
			interpreter.initialize();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		System.out.println("Final:\n" + interpreter);
		
		/*
		System.out.println("\nNext Test");
		for(StatementNode statementNode : AST.getStatementNodes())
		{
			System.out.println(statementNode + " - next: " + statementNode.getNext());
		}
		/**/
	}
}