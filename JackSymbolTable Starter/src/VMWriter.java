import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class VMWriter {
	private FileOutputStream output;
	private BufferedWriter writer;
	public static final int CONST = 0;
	public static final int ARG = 1;
	public static final int LOCAL = 2;
	public static final int STATIC = 3;
	public static final int THIS = 4;
	public static final int THAT = 5;
	public static final int POINTER = 6;
	public static final int TEMP = 7;
	private FileWriter vmFile;

	public VMWriter(String basename) {
		try {
			vmFile = new FileWriter(basename + ".vm");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Write a sequence of VM code to the XML file.
	public void write(String code) {
		try {
			vmFile.write(code + '\n');
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void close() {
		try {
			vmFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void writePop(String segment, Integer integer) {
		try {
			vmFile.write("pop " + segment + " " + integer + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void writePush(String segment, Integer integer) {
		try {
			vmFile.write("push " + segment + " " + integer + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void writeArtithmetic(char command) {
		switch (command) {
		case '+':
			write("add");
			break;
		case '-':
			write("sub");
			break;
		case '*':
			write("call Math.multiply 2");
			break;
		case '/':
			write("call Math.divide 2");
			break;
		case '<':
			write("lt");
			break;
		case '>':
			write("gt");
			break;
		case '&':
			write("and");
			break;
		case '|':
			write("or");
			break;
		case '=':
			write("eq");
			break;

//not case
//case '~': write("not"); break;
		}
	}

	public void writeCall(String name, int nArgs) {
		write("call " + name + " " + nArgs);
	}

	public void writeFunction(String name, int nLocals) {
		write("function " + name + " " + nLocals);
	}

	public void writeReturn() {
		write("return");
	}

	public void writeLabel(String label) {
		try {
			vmFile.write("label " + label + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void writeGoto(String label) {
		try {
			vmFile.write("goto " + label + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void writeIf(String label) {
		try {
			vmFile.write("if-goto " + label + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}