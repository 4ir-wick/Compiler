import java.util.ArrayList;
import java.util.HashMap;

public class Lexer
{
	private HashMap<String, Token.Type> knownWords;

	public Lexer()
	{
		knownWords = new HashMap<String, Token.Type>();
		knownWords.put("PRINT", Token.Type.PRINT);
		knownWords.put("READ", Token.Type.READ);
		knownWords.put("DATA", Token.Type.DATA);
		knownWords.put("INPUT", Token.Type.INPUT);
		knownWords.put("GOSUB", Token.Type.GOSUB);
		knownWords.put("RETURN", Token.Type.RETURN);
		knownWords.put("FOR", Token.Type.FOR);
		knownWords.put("TO", Token.Type.TO);
		knownWords.put("STEP", Token.Type.STEP);
		knownWords.put("NEXT", Token.Type.NEXT);
		knownWords.put("IF", Token.Type.IF);
		knownWords.put("THEN", Token.Type.THEN);
		knownWords.put("RANDOM", Token.Type.FUNCTIONNAME);
		knownWords.put("LEFT$", Token.Type.FUNCTIONNAME);
		knownWords.put("RIGHT$", Token.Type.FUNCTIONNAME);
		knownWords.put("MID$", Token.Type.FUNCTIONNAME);
		knownWords.put("NUM$", Token.Type.FUNCTIONNAME);
		knownWords.put("VAL", Token.Type.FUNCTIONNAME);
		knownWords.put("VAL%", Token.Type.FUNCTIONNAME);
	}

	public ArrayList<Token> lex(String input) throws Exception
	{
		ArrayList<Token> tokens = new ArrayList<Token>();
		int state = 1;
		
		int inputLength = input.length();
		
		for(int i = 0; i <= inputLength; i++)
		{
			if(i == inputLength) // if no characters, imediately skip to end of line (EOL)
				state = 8;
			
			char c = 0;
			if(state < 8 && state > 0) // if not EOL or ERROR
			{
				c = input.charAt(i);
			}
			
			// start
			if(state == 1) // put us in different states based on the character
			{
				if(c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '<' || c == ',')
					state = 2;
				else if(c == '>' || c == '=')
					state = 3;
				else if(c >= 48 && c <= 57 || c == '.')
					state = 4;
				else if(c == '"')
					state = 5;
				else if((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
					state = 6;
				else if(c == ' ' || c == '\t')
					state = 7;
				else if(c == 0)
					state = 8;
				else
					state = -1;
			}
			
			int tokensSize = tokens.size();
			
			Token currentToken = new Token();
			Token.Type currentTokenType = null;
			int currentTokenIndex = tokensSize - 1;
			if(tokensSize > 0) // if we have tokens
			{
				currentToken = tokens.get(currentTokenIndex); // grab the most recent token from our tokens
				currentTokenType = currentToken.getType();
			}
			
			// stand alone symbols
			if(state == 2)
			{
				if(c == '+') // TODO reduce use of checking for ignored tokens
					if(currentTokenType == Token.Type.IGNORED) // if we have an ignored token, then we need to update the token
						tokens.set(currentTokenIndex, new Token(Token.Type.PLUS));
					else
						tokens.add(new Token(Token.Type.PLUS)); // anything else will add the token regardless
				else if(c == '-')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.MINUS));
					else
						tokens.add(new Token(Token.Type.MINUS));
				else if(c == '*')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.TIMES));
					else
						tokens.add(new Token(Token.Type.TIMES));
				else if(c == '/')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.DIVIDE));
					else
						tokens.add(new Token(Token.Type.DIVIDE));
				else if(c == '(')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.LPAREN));
					else
						tokens.add(new Token(Token.Type.LPAREN));
				else if(c == ')')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.RPAREN));
					else
						tokens.add(new Token(Token.Type.RPAREN));
				else if(c == '<')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.LESSTHAN));
					else
						tokens.add(new Token(Token.Type.LESSTHAN));
				else if(c == ',')
					if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.COMMA));
					else
						tokens.add(new Token(Token.Type.COMMA));
				state = 1; // only one symbol is used in this state so return to start regardless
			}
			// symbol combos
			if(state == 3)
			{
				if(c == '>')
				{
					if(currentTokenType == Token.Type.LESSTHAN) // checks the last token to see if we have a special combo
						tokens.set(currentTokenIndex, new Token(Token.Type.NOTEQUALS));
					else if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.GREATERTHAN));
					else
						tokens.add(new Token(Token.Type.GREATERTHAN));
				}
				else if(c == '=')
				{
					if(currentTokenType == Token.Type.LESSTHAN)
						tokens.set(currentTokenIndex, new Token(Token.Type.LESSTHANEQUALS));
					else if(currentTokenType == Token.Type.GREATERTHAN)
						tokens.set(currentTokenIndex, new Token(Token.Type.GREATERTHANEQUALS));
					else if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.EQUALS));
					else
						tokens.add(new Token(Token.Type.EQUALS));
				}
				state = 1; // only one symbol is used in this state so return to start regardless
			}
			// numbers
			else if(state == 4)
			{
				if(c >= 48 && c <= 57)
				{
					if(currentTokenType == Token.Type.MINUS) // include the minus in our number
						tokens.set(currentTokenIndex, new Token("-" + c, Token.Type.NUMBER)); // update the token with a "-" and a number
					else if(currentTokenType == Token.Type.NUMBER) // if we already have the same one than we can append our number
					{
						currentToken.appendValue(c);
						tokens.set(currentTokenIndex, currentToken);
					}
					else if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(c, Token.Type.NUMBER));
					else
						tokens.add(new Token(c, Token.Type.NUMBER));
				}
				else if(c == '.')
				{
					if(currentTokenType == Token.Type.NUMBER && !currentToken.getValue().contains(".")) // only append the "." if our token is actually a number and we don't already have a "."
					{
						currentToken.appendValue(c);
						tokens.set(currentTokenIndex, currentToken);
					}
					else
					{
						i--; // handle this character again
						state = -1; // process error because a regular "." is not a valid token
					}
				}
				else // any other character
				{
					i--; // handle this character again
					state = 1; // we have completed our number
				}
			}
			// strings
			else if(state == 5)
			{
				if(c == '"')
				{
					
					if(currentTokenType == Token.Type.STRING) // if we already had a string then our string is completed
						state = 1; // return to start
					else if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(Token.Type.STRING));
					else
						tokens.add(new Token(Token.Type.STRING));
				}
				else // anything that isn't a '"' while in the string state will be put in our string
				{
					currentToken.appendValue(c);
					tokens.set(currentTokenIndex, currentToken);
				}
			}
			// words
			else if(state == 6)
			{
				if((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == '$' || c == '%')
					if(currentTokenType == Token.Type.WORD) // if already a word then add to the word
					{
						if(!currentToken.getValue().endsWith("$") && !currentToken.getValue().endsWith("%")) // if the word doesn't end with $ or %
						{
							currentToken.appendValue(c);
							tokens.set(currentTokenIndex, currentToken);
						}
						else
						{
							i--; // handle this character again
							state = -1; // cannot add character to word that ends with a $ or %
						}
					}
					else if(currentTokenType == Token.Type.IGNORED)
						tokens.set(currentTokenIndex, new Token(c, Token.Type.WORD));
					else if(currentTokenType == Token.Type.LABEL) // if trying to add a letter to a token that is already a label
					{
						i--; // handle this character again
						state = -1; // cannot add character to label
					}
					else
						tokens.add(new Token(c, Token.Type.WORD));
				else if(c == ':')
				{
					if(currentTokenType == Token.Type.WORD)
					{
						currentToken.setType(Token.Type.LABEL);
						tokens.set(currentTokenIndex, currentToken);
					}
					else
					{
						i--; // handle this character again
						state = -1; // ":" is ONLY valid at the end of a word
					}
				}
				else // indicates end of the word
				{
					// TODO make the check for known words a function because it is used more than once
					// check if our word is a keyword
					for(String key : knownWords.keySet())
					{
						if(key.equals(currentToken.getValue()))
						{
							tokens.set(currentTokenIndex, new Token(currentToken.getValue(), knownWords.get(key)));
						}
					}
					if(tokens.get(currentTokenIndex).getType() == Token.Type.WORD) // if we are still a word then it must be an identifier
					{
						currentToken.setType(Token.Type.IDENTIFIER);
						tokens.set(currentTokenIndex, currentToken);
					}
					i--; // handle this character again
					state = 1; // return to start because word has been processed
				}
			}
			// spaces or tabs
			else if(state == 7)
			{
				if(currentTokenType != Token.Type.IGNORED) // if not already an ignored token
					tokens.add(new Token(Token.Type.IGNORED)); // create a new ignored token (this is what all of the ignored checks are for)
															   // (we need to check for ignored everywhere because we don't want to leave a node as an ignored)
				state = 1; // the space or tab has been processed, return to start
			}
			// end of line
			else if(state == 8)
			{
				if(currentTokenType == Token.Type.IGNORED)
					tokens.set(currentTokenIndex, new Token(Token.Type.EndOfLine));
				else if(currentTokenType == Token.Type.WORD) // perform the keyword check and apply the keyword or set to identifier
				{
					for(String key : knownWords.keySet())
					{
						if(key.equals(currentToken.getValue()))
						{
							tokens.set(currentTokenIndex, new Token(currentToken.getValue(), knownWords.get(key)));
						}
					}
					if(tokens.get(currentTokenIndex).getType() == Token.Type.WORD)
					{
						currentToken.setType(Token.Type.IDENTIFIER);
						tokens.set(currentTokenIndex, currentToken);
					}
					tokens.add(new Token(Token.Type.EndOfLine));
				}
				else
					tokens.add(new Token(Token.Type.EndOfLine));
			}
			// error
			else if(state == -1)
				throw new Exception("Invalid character at index " + (i)); // throw an error
		}
		return tokens;
	}
}