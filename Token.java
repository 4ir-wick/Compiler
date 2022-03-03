public class Token
{
	public enum Type
	{
		PLUS,
		MINUS,
		TIMES,
		DIVIDE,
		LPAREN,
		RPAREN,
		LESSTHAN,
		GREATERTHAN,
		COMMA,
		EQUALS,
		NOTEQUALS,
		LESSTHANEQUALS,
		GREATERTHANEQUALS,
		NUMBER,
		STRING,
		WORD,
		LABEL,
		PRINT,
		READ,
		DATA,
		INPUT,
		GOSUB,
		RETURN,
		FOR,
		TO,
		STEP,
		NEXT,
		IF,
		THEN,
		FUNCTIONNAME,
		IDENTIFIER,
		IGNORED,
		EndOfLine
	}
	private String value;
	private Type type;
	
	public Token()
	{
		this("", null);
	}
	
	public Token(String value)
	{
		this(value, null);
	}
	
	public Token(char value)
	{
		this(Character.toString(value), null);
	}
	
	public Token(Type type)
	{
		this("", type);
	}
	
	public Token(char value, Type type)
	{
		this(Character.toString(value), type);
	}
	
	public Token(String value, Type type)
	{
		setValue(value);
		setType(type);
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public void appendValue(String value)
	{
		this.value += value;
	}
	
	public void appendValue(char value)
	{
		appendValue(Character.toString(value));
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public String toString()
	{
		String str = type.toString();
		if(this.getType() == Type.NUMBER || this.getType() == Type.LABEL || this.getType() == Type.STRING) // only show parenthesis for specific types
		{
			str += "(" + value + ")";
		}
		return str;
	}
}