import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;


public class JackTokenizer {

	
	private PushbackReader reader;
	private boolean hasMoreTokens;
	private char ch;
	private TokenType tokenType;
	private KeyWord keyWord;
	private char symbol;
	private String identifier;


	public JackTokenizer(File src) {
		hasMoreTokens = true;

		try {
			reader = new PushbackReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	public boolean hasMoreTokens() {
		return hasMoreTokens;
	}
	
	public TokenType tokenType(){
		return tokenType;
	}
	
	public KeyWord keyWord(){
		return keyWord;
	}
	
	public char symbol(){
		return symbol;
	}
	
	public String identifier(){
		return identifier;
	}
	
	public int intVal(){
		try{
			return Integer.parseInt(identifier);
		}catch (NumberFormatException e){
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}
	
	// Return string without the enclosing quotes.
	public String stringVal(){
		return identifier;
	}
	
	private void getNextChar(){
		try{
			int data = reader.read();
			if (data == -1)
				hasMoreTokens = false;
			else
				ch = (char)data;
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void unread(){
		try{
			reader.unread(ch);
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
		

	public void advance() {
		// Complete to advance over next token, setting values in the fields
		State state = State.START;
		int counter = 0;
		while (hasMoreTokens()) {
			getNextChar();
			switch (state) {
			case START:
				tokenType = TokenType.STRING_CONST;
				identifier = "";
				if (ch == '/') {
					state = State.SLASH;
					// identifier += ch;
					break;
				}

				if (ch == '{' || ch == '}' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '.' || ch == ','
						|| ch == ';' || ch == '+' || ch == '_' || ch == '*' || ch == '&' || ch == '|' || ch == '<'
						|| ch == '>' || ch == '=' || ch == '-' || ch =='~') {
					symbol = ch;
					tokenType = TokenType.SYMBOL;
					return;

				}
				if (ch == '"') {
					state = State.STRING;
					tokenType = TokenType.STRING_CONST;
					break;

				}
				if (Character.isDigit(ch)) {
					state = State.NUMBER;
					identifier += ch;
					tokenType = TokenType.INT_CONST;
					break;
				}

				if (Character.isLetter(ch) || ch == '_' || Character.isDigit(ch)) {
					identifier += ch;
					state = State.WORD;
					tokenType = TokenType.KEYWORD;
					break;
				} else {
					break;
				}

			case STRING:
				if (ch == '"') {
					return;

				} else {
					identifier += ch;
					break;
				}

			case NUMBER:
				if (Character.isDigit(ch)) {
					tokenType = TokenType.INT_CONST;
					identifier += ch;
					break;
				} else {
					unread();
					return;
				}
			case WORD:
				if (Character.isLetter(ch) || ch == '_' || Character.isDigit(ch)) {
					// tokenType = TokenType.IDENTIFIER;
					identifier += ch;
					break;
				} else {
					unread();
					switch (identifier) {

					case "class":
						keyWord = KeyWord.CLASS;
						return;
					case "function":
						keyWord = KeyWord.FUNCTION;
						return;
					case "constructor":
						keyWord = KeyWord.CONSTRUCTOR;
						return;
					case "method":
						keyWord = KeyWord.METHOD;
						return;
					case "int":
						keyWord = KeyWord.INT;
						return;
					case "boolean":
						keyWord = KeyWord.BOOLEAN;
						return;
					case "char":
						keyWord = KeyWord.CHAR;
						return;
					case "void":
						keyWord = KeyWord.VOID;
						return;

					case "static":
						keyWord = KeyWord.STATIC;
						return;

					case "field":
						keyWord = KeyWord.FIELD;
						return;

					case "if":
						keyWord = KeyWord.IF;
						return;

					case "var":
						keyWord = KeyWord.VAR;
						return;

					case "let":
						keyWord = KeyWord.LET;
						return;

					case "do":
						keyWord = KeyWord.DO;
						return;

					case "else":
						keyWord = KeyWord.ELSE;
						return;

					case "while":
						keyWord = KeyWord.WHILE;
						return;

					case "return":
						keyWord = KeyWord.RETURN;
						return;
					case "true":
						keyWord = KeyWord.TRUE;
						return;

					case "false":
						keyWord = KeyWord.FALSE;
						return;

					case "null":
						keyWord = KeyWord.NULL;
						return;

					case "this":
						keyWord = KeyWord.THIS;
						return;

					default:
						tokenType = TokenType.IDENTIFIER;
						return;

					}
				}
			case SLASH:
				if (ch == '/') {
					state = State.COMMENT;
					// identifier += ch;
					break;
				}
				if (ch == '*') {
					state = State.COMMENT1;
					// identifier += ch;
					break;
				}

				else {
					tokenType = TokenType.SYMBOL;
					symbol = '/';
					unread();
					return;
				}

			case COMMENT:
				if (ch == '\n') {
					state = State.START;
					unread();
					break;
				}

				else {
					// unread();
					state = State.COMMENT;
					break;
				}

			case COMMENT1:
				if (counter >= 1) {
					if (ch == '/') {
						//unread();
						state = State.START;
						break;
					}
				}
				if (ch == '*') {
					counter = counter + 1;
					break;
				}

				counter = 0;
				break;

			}

		}
	}
}


		// Complete to advance over next token, setting values in the fields
		



