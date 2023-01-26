import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class CompilationEngine {
	JackTokenizer tokenizer = null;
	XMLWriter xmlWriter = null;
	SymbolTable symbolTable = null;
	VMWriter vmWriter = null;

	KeyWord key;
	String name;
	String ID2;
	STKind kind;
	String ID;
	String funcName;
	String fileId;
	Integer numLocals = 0;
	// if counter
	int Ifs = 0;
	String typeVar;
	// while counter
	int While1 = 0;
	String arrayID;
	String x;
	boolean bool;
	String ID3;
	String MName;
	String temp1;
	Integer Exp = 1;

	public CompilationEngine(String filename, File file) {

		tokenizer = new JackTokenizer(file);
		xmlWriter = new XMLWriter(filename);
		symbolTable = new SymbolTable();
		vmWriter = new VMWriter(filename);
		x = tokenizer.identifier();
		Ifs = 0;
		While1 = 0;

	}

	public String Seg(STKind x) {
		if (x == STKind.VAR) {
			return "local";

		} else if (x == STKind.ARG) {
			return "argument";
		} else if (x == STKind.FIELD) {
			return "this";
		} else if (x == STKind.STATIC) {
			return "static";
		} else {
			return null;
		}
	}

	public void close() {
		try {
			xmlWriter.close();
			vmWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void symbolInfo(String use, String identifier) {
		xmlWriter.writeCode(use + " " + identifier + " type:" + symbolTable.typeOf(identifier) + " kind:"
				+ symbolTable.kindOf(identifier) + " index:" + symbolTable.indexOf(identifier) + '\n');
	}

	public void compileClass() {
		xmlWriter.writeCode("<class>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<keyword> " + tokenizer.identifier() + " </keyword> \n");
		tokenizer.advance();
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
		fileId = tokenizer.identifier();
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();

		while (tokenizer.keyWord() == KeyWord.STATIC || tokenizer.keyWord() == KeyWord.FIELD) {
			compileClassVarDec();
		}
		while (tokenizer.keyWord() == KeyWord.CONSTRUCTOR || tokenizer.keyWord() == KeyWord.FUNCTION
				|| tokenizer.keyWord() == KeyWord.METHOD || tokenizer.keyWord() == KeyWord.VOID
				|| tokenizer.tokenType() == TokenType.IDENTIFIER) {
			compileSubroutineDec();
		}
		xmlWriter.writeCode("<symbol> } </symbol> \n");
		tokenizer.advance();
		xmlWriter.writeCode("</class>\n");
		close();

	}

	public void compileClassVarDec() {
		xmlWriter.writeCode("<classVarDec>\n");
		if (tokenizer.keyWord() == KeyWord.STATIC) {
			xmlWriter.writeCode("<keyword> static </keyword> \n");
			kind = STKind.STATIC;
			tokenizer.advance();
		} else {
			xmlWriter.writeCode("<keyword> field </keyword> \n");
			key = tokenizer.keyWord();
			kind = STKind.FIELD;
			tokenizer.advance();

		}
		compileType();
		symbolTable.define(tokenizer.identifier(), typeVar, kind);
		String x = tokenizer.identifier();
		xmlWriter.writeCode("Define " + x + " type: " + symbolTable.typeOf(x) + " kind: " + symbolTable.kindOf(x)
				+ " index: " + symbolTable.indexOf(x) + "\n");
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier> \n");
		tokenizer.advance();
		compileVarNameList();
		xmlWriter.writeCode("<symbol> ; </symbol> \n");
		tokenizer.advance();

		xmlWriter.writeCode("</classVarDec>\n");

	}

	public void compileType() {
		String type = tokenizer.identifier();
		//swtich case
		switch (type) {
		case "int":
			xmlWriter.writeCode("<keyword> int </keyword> \n");
			typeVar = "int";
			tokenizer.advance();
			break;
		case "char":
			xmlWriter.writeCode("<keyword> char </keyword>\n");
			typeVar = "char";
			tokenizer.advance();
			break;
		case "boolean":
			xmlWriter.writeCode("<keyword> boolean </keyword>\n");
			typeVar = "boolean";
			tokenizer.advance();
			break;
		default:
			xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
			typeVar = tokenizer.identifier();
			tokenizer.advance();
			break;
		}
	}
	public void compileVarNameList() {
		while (tokenizer.symbol() == ',') {
			tokenizer.advance();
			symbolTable.define(tokenizer.identifier(), typeVar, kind);
			xmlWriter.writeCode("<symbol> , </symbol> \n");
			symbolInfo("Define", tokenizer.identifier());
			xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
			numLocals++;
			tokenizer.advance();
		}
	}
	public void compileSubroutineDec() {
		symbolTable.startSubroutine();
		xmlWriter.writeCode("<subroutineDec>\n");
		While1 = 0;
		Ifs = 0;
		if (tokenizer.identifier().equals("constructor")) {
			xmlWriter.writeCode("<keyword> constructor </keyword>\n");
			key = KeyWord.CONSTRUCTOR;
			tokenizer.advance();
		}
		else if (tokenizer.identifier().equals("function")) {
			xmlWriter.writeCode("<keyword> function </keyword>\n");
			key = KeyWord.FUNCTION;
			tokenizer.advance();
		}
		else if (tokenizer.identifier().equals("method")) {
			xmlWriter.writeCode("<keyword> method </keyword>\n");
			symbolTable.define("this", typeVar, STKind.ARG);
			key = KeyWord.METHOD;
			tokenizer.advance();
		}
		if (tokenizer.identifier().equals("void")) {
			xmlWriter.writeCode("<keyword> void </keyword>\n");
			tokenizer.advance();
		}
		else {
			compileType();
		}
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
		funcName = tokenizer.identifier();
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		compileParameterList();
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		compileSubroutineBody();
		xmlWriter.writeCode("</subroutineDec> \n");
	}
	public void compileParameterList() {
		xmlWriter.writeCode("<parameterList>\n");
		if (tokenizer.symbol() != ')') {
			compileType();
			kind = STKind.ARG;
			symbolTable.define(tokenizer.identifier(), typeVar, kind);
			symbolInfo("Define", tokenizer.identifier());
			xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
			tokenizer.advance();
			while (tokenizer.symbol() == ',') {
				xmlWriter.writeCode("<symbol> , </symbol> \n");
				tokenizer.advance();
				compileType();
				symbolTable.define(tokenizer.identifier(), typeVar, kind);
				symbolInfo("Define", tokenizer.identifier());
				xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
				tokenizer.advance();
			}
		}
		xmlWriter.writeCode("</parameterList>\n");
	}
	public void compileSubroutineBody() {
		xmlWriter.writeCode("<subroutineBody>\n");
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		numLocals = 0;
		while (!(tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.LET
				|| tokenizer.keyWord() == KeyWord.IF || tokenizer.keyWord() == KeyWord.WHILE
				|| tokenizer.keyWord() == KeyWord.DO || tokenizer.keyWord() == KeyWord.RETURN))) {
			compileVarDec();
		}
		vmWriter.writeFunction(fileId + "." + funcName, numLocals);
		if (key == KeyWord.CONSTRUCTOR) {
			int field = symbolTable.varCount(STKind.FIELD);
			vmWriter.writePush("constant", field);
			vmWriter.writeCall("Memory.alloc", 1);
			vmWriter.writePop("pointer", 0);
		} else if (key == KeyWord.METHOD) {
			vmWriter.writePush("argument", 0);
			vmWriter.writePop("pointer", 0);
		}
		numLocals = 0;
		compileStatements();
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</subroutineBody>\n");
	}
	public void compileVarDec() {
		xmlWriter.writeCode("<varDec> \n");
		if (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.VAR)) {
			xmlWriter.writeCode("<keyword> var </keyword>\n");
			kind = STKind.VAR;
			tokenizer.advance();
		}
		compileType();
		numLocals++;
		symbolTable.define(tokenizer.identifier(), typeVar, kind);
		symbolInfo("Define", tokenizer.identifier());
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier> \n");
		tokenizer.advance();
		compileVarNameList();
		xmlWriter.writeCode("<symbol> ; </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</varDec> \n");
	}

	public void compileStatements() {
		xmlWriter.writeCode("<statements>\n");
		while (tokenizer.tokenType() == TokenType.KEYWORD) {
			if (tokenizer.keyWord() == KeyWord.LET) {
				compileLetStatement();
			} else if (tokenizer.keyWord() == KeyWord.IF) {
				compileIfStatement();
			} else if (tokenizer.keyWord() == KeyWord.WHILE) {
				compileWhileStatement();
			} else if (tokenizer.keyWord() == KeyWord.DO) {
				compileDoStatement();
			} else if (tokenizer.keyWord() == KeyWord.RETURN) {

				compileReturnStatement();
			}

		}
		xmlWriter.writeCode("</statements>\n");
	}

	public void compileLetStatement() {
		xmlWriter.writeCode("<letStatement>\n");
		if (tokenizer.keyWord() == KeyWord.LET) {
			xmlWriter.writeCode("<keyword> let </keyword>\n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
		symbolInfo("Use", tokenizer.identifier());
		String Var = tokenizer.identifier();
		tokenizer.advance();
		if (tokenizer.symbol() == '[') {
			xmlWriter.writeCode("<symbol> [ </symbol> \n");
			tokenizer.advance();
			MName = tokenizer.identifier();
			compileExpression();
			xmlWriter.writeCode("<symbol> ] </symbol> \n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("<symbol> = </symbol> \n");
		tokenizer.advance();
		MName = tokenizer.identifier();
		compileExpression();
		xmlWriter.writeCode("<symbol> ; </symbol>\n");
		tokenizer.advance();
		vmWriter.writePop(Seg(symbolTable.kindOf(Var)), symbolTable.indexOf(Var));
		xmlWriter.writeCode("</letStatement>\n");
	}
	public void compileIfStatement() {
		xmlWriter.writeCode("<ifStatement>\n");
		if (tokenizer.keyWord() == KeyWord.IF) {
			xmlWriter.writeCode("<keyword> if </keyword>\n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		compileExpression();
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		vmWriter.writeIf("IF_TRUE" + Ifs);
		vmWriter.writeGoto("IF_FALSE" + Ifs);
		vmWriter.writeLabel("IF_TRUE" + Ifs);
		int Ifs2 = Ifs;
		Ifs++;
		
		compileStatements();
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();
		if (tokenizer.keyWord() == KeyWord.ELSE) {

			vmWriter.writeGoto("IF_END" + Ifs2);
			vmWriter.writeLabel("IF_FALSE" + Ifs2);
			xmlWriter.writeCode("<keyword> else </keyword>\n");

			tokenizer.advance();
			xmlWriter.writeCode("<symbol> { </symbol>\n");
			tokenizer.advance();
			compileStatements();

			xmlWriter.writeCode("<symbol> } </symbol>\n");
			tokenizer.advance();
			vmWriter.writeLabel("IF_END" + Ifs2);

		} else {

			vmWriter.writeLabel("IF_FALSE" + Ifs2);
		}
		xmlWriter.writeCode("</ifStatement>\n");
	}

	public void compileWhileStatement() {

		xmlWriter.writeCode("<whileStatement>\n");
		if (tokenizer.keyWord() == KeyWord.WHILE) {
			xmlWriter.writeCode("<keyword> while </keyword>\n");
			tokenizer.advance();

		}
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		vmWriter.writeLabel("WHILE_EXP" + While1);
		int While3 = While1;
		compileExpression();
		vmWriter.write("not");
		
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		vmWriter.writeIf("WHILE_END" + While1);
		int While2 = While1;
		While1++;
		compileStatements();
		vmWriter.writeGoto("WHILE_EXP" + While3);
		vmWriter.writeLabel("WHILE_END" + While2);
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</whileStatement>\n");

	}

	public void compileDoStatement() {
		xmlWriter.writeCode("<doStatement>\n");
		if (tokenizer.keyWord() == KeyWord.DO) {
			xmlWriter.writeCode("<keyword> do </keyword>\n");
			tokenizer.advance();
		}

		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
		ID = tokenizer.identifier();
		MName = tokenizer.identifier();
		tokenizer.advance();
		compileSubroutineCall();
		vmWriter.writePop("temp", 0);
		xmlWriter.writeCode("<symbol> ; </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</doStatement>\n");
	}

	public void compileReturnStatement() {
		xmlWriter.writeCode("<returnStatement>\n");
		if (tokenizer.keyWord() == KeyWord.RETURN) {
			xmlWriter.writeCode("<keyword> return </keyword>\n");
			tokenizer.advance();
		}
		if (!(tokenizer.symbol() == ';' && tokenizer.tokenType() == TokenType.SYMBOL)) {
			compileExpression();
		} else {
			vmWriter.writePush("constant", 0);
		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		vmWriter.writeReturn();
		xmlWriter.writeCode("</returnStatement>\n");

	}

	public void compileExpression() {
		xmlWriter.writeCode("<expression>\n");
		char sym = 'a';
		compileTerm();
		sym = tokenizer.symbol();
		while (tokenizer.symbol() == '+' || tokenizer.symbol() == '-' || tokenizer.symbol() == '*'
				|| tokenizer.symbol() == '/' || tokenizer.symbol() == '&' || tokenizer.symbol() == '|'
				|| tokenizer.symbol() == '<' || tokenizer.symbol() == '>' || tokenizer.symbol() == '=') {
			if (tokenizer.symbol() == '<') {
				xmlWriter.writeCode("<symbol> &lt; </symbol> \n");
				sym = tokenizer.symbol();
				tokenizer.advance();
			} else if (tokenizer.symbol() == '>') {
				xmlWriter.writeCode("<symbol> &gt; </symbol> \n");
				sym = tokenizer.symbol();
				tokenizer.advance();
			} else if (tokenizer.symbol() == '&') {
				xmlWriter.writeCode("<symbol> &amp; </symbol> \n");
				sym = tokenizer.symbol();
				tokenizer.advance();
			} else {
				xmlWriter.writeCode("<symbol> " + tokenizer.symbol() + " </symbol>\n");
				tokenizer.advance();
			}
			char symbol = tokenizer.symbol();
			compileTerm();
			if (symbol == '+') {
				vmWriter.writeArtithmetic('+');
			} else if (sym == '<') {
				vmWriter.writeArtithmetic('<');
			} else if (sym == '>') {
				vmWriter.writeArtithmetic('>');
			} else if (sym == '&') {
				vmWriter.writeArtithmetic('&');
			} else if (symbol == '-') {
				vmWriter.writeArtithmetic('-');
			} else if (symbol == '=') {
				vmWriter.writeArtithmetic('=');
			} else if (sym == '*') {
				vmWriter.writeCall("Math.multiply", 2);
			} else if (sym == '/') {
				vmWriter.writeCall("Math.divide", 2);
			} else if (sym == '|') {
				vmWriter.writeArtithmetic('|');
			} else if (sym == '=') {
				vmWriter.writeArtithmetic('=');
			} else if (sym == '+') {
				vmWriter.writeArtithmetic('+');
			}
		}
		xmlWriter.writeCode("</expression>\n");
	}

	public void compileTerm() {
		xmlWriter.writeCode("<term>\n");
		if (tokenizer.tokenType() == TokenType.INT_CONST) {
			xmlWriter.writeCode("<integerConstant> " + tokenizer.identifier() + " </integerConstant>\n");
			vmWriter.writePush("constant", tokenizer.intVal());
			tokenizer.advance();
		} else if (tokenizer.tokenType() == TokenType.STRING_CONST) {
			xmlWriter.writeCode("<stringConstant> " + tokenizer.identifier() + " </stringConstant>\n");
			String s = tokenizer.stringVal();
			vmWriter.writePush("constant", s.length());
			vmWriter.writeCall("String" + "." + "new", 1);
			for (int i = 0; i < s.length(); i++) {
				vmWriter.writePush("constant", (int) s.charAt(i));
				vmWriter.writeCall("String.appendChar ", 2);
			}
			tokenizer.advance();
		} else if (tokenizer.keyWord() == KeyWord.TRUE || tokenizer.keyWord() == KeyWord.FALSE
				|| tokenizer.keyWord() == KeyWord.NULL || tokenizer.keyWord() == KeyWord.THIS) {
			xmlWriter.writeCode("<keyword> " + tokenizer.identifier() + " </keyword>\n");
			if (tokenizer.keyWord() == KeyWord.TRUE) {
				vmWriter.writePush("constant", 0);
				vmWriter.write("not");
				tokenizer.advance();
			} else if (tokenizer.keyWord() == KeyWord.FALSE) {
				vmWriter.writePush("constant", 0);
				tokenizer.advance();
			} else if (tokenizer.keyWord() == KeyWord.NULL) {
				tokenizer.advance();
			} else if (tokenizer.keyWord() == KeyWord.THIS) {
				vmWriter.writePush("pointer", 0);
				tokenizer.advance();
			}

		} else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '-'
				|| tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '~') {

			char neg = ' ';
			char not = ' ';
			if (tokenizer.symbol() == '~') {
				not = tokenizer.symbol();
			} else if (tokenizer.symbol() == '-') {
				neg = tokenizer.symbol();
			}
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			compileTerm();
			if (not == '~') {
				vmWriter.write("not");
			}
			if (neg == '-') {
				vmWriter.write("neg");
			}
		} else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
			xmlWriter.writeCode("<symbol> ( </symbol> \n");
			tokenizer.advance();
			compileExpression();
			xmlWriter.writeCode("<symbol> ) </symbol> \n");
			tokenizer.advance();
		} else if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
			xmlWriter.writeCode("<identifier> " + tokenizer.identifier() + " </identifier>\n");
			ID = tokenizer.identifier();
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') {
				symbolInfo("Use", ID);
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				compileExpression();
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
			} else if (tokenizer.symbol() == '.' || tokenizer.symbol() == '(') {
				compileSubroutineCall();
			} else if (tokenizer.symbol() == '.') {
				if (symbolTable.indexOf(ID) != -1) {
					symbolInfo("Use", ID);
				}
			} else {
				symbolInfo("Use", ID);
				vmWriter.writePush(Seg(symbolTable.kindOf(ID)), symbolTable.indexOf(ID));
			}

		}

		xmlWriter.writeCode("</term>\n");

	}

	public void compileSubroutineCall() {
		numLocals = 0;
		if (tokenizer.symbol() == '(') {
			vmWriter.writePush("pointer", 0);
			xmlWriter.writeCode("<symbol> ( </symbol> \n");
			tokenizer.advance();
			numLocals++;
			if (!(tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == ')'))) {
				compileExpressionList();
			} else {
				xmlWriter.writeCode("<expressionList>\n");
				xmlWriter.writeCode("</expressionList>\n");
			}
			vmWriter.writeCall(fileId + "." + MName, numLocals);
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();

		} else if (tokenizer.symbol() == '.') {
			if (symbolTable.indexOf(ID) != -1) {
				symbolInfo("Use", ID);
				vmWriter.writePush(Seg(symbolTable.kindOf(ID)), symbolTable.indexOf(ID));
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");
				ID2 = tokenizer.identifier();
				tokenizer.advance();
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				if (!(tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == ')'))) {
					compileExpressionList();
					numLocals++;
				} else {
					numLocals++;
					xmlWriter.writeCode("<expressionList>\n");
					xmlWriter.writeCode("</expressionList>\n");
				}

				vmWriter.writeCall(symbolTable.typeOf(MName) + "." + ID2, numLocals);

				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");

				tokenizer.advance();

			} else {

				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");

				tokenizer.advance();

				xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");

				String temp2 = ID;

				String temp3 = tokenizer.identifier();
				tokenizer.advance();
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				if (!(tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == ')'))) {
					compileExpressionList();
				} else {
					xmlWriter.writeCode("<expressionList>\n");
					xmlWriter.writeCode("</expressionList>\n");
				}
				vmWriter.writeCall(temp2 + "." + temp3, numLocals);
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
			}
		}
	}

	public void compileExpressionList() {
		xmlWriter.writeCode("<expressionList>\n");
		compileExpression();
		numLocals++;
		while (tokenizer.symbol() == ',') {
			numLocals++;
			xmlWriter.writeCode("<symbol> , </symbol> \n");
			tokenizer.advance();
			compileExpression();
		}
		xmlWriter.writeCode("</expressionList>\n");
	}
}
