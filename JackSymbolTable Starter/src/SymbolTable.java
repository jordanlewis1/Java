import java.util.Hashtable;

public class SymbolTable {

	int Stcount = 0;
	int Fcount = 0;
	int Arg = 0;
	int Var = 0;

	Hashtable<String, STEntry> classes;
	Hashtable<String, STEntry> methods;

	public SymbolTable() {

		classes = new Hashtable<String, STEntry>();
		methods = new Hashtable<String, STEntry>();

	}

	public void startSubroutine() {

		Arg = 0;
		Var = 0;
		methods.clear();

	}

	public void define(String name, String type, STKind kind) {
		if (kind == STKind.STATIC) {

			STEntry staticEnt = new STEntry(type, kind, Stcount);
			classes.put(name, staticEnt);
			Stcount++;

		} else if (kind == STKind.FIELD) {

			STEntry fieldEnt = new STEntry(type, kind, Fcount);
			classes.put(name, fieldEnt);
			Fcount++;

		} else if (kind == STKind.ARG) {

			STEntry arg = new STEntry(type, kind, Arg);
			methods.put(name, arg);
			Arg++;

		} else {

			STEntry var = new STEntry(type, kind, Var);
			methods.put(name, var);
			Var++;

		}

	}

	public int varCount(STKind kind) {

		if (kind == STKind.STATIC)
			return Stcount;
		else if (kind == STKind.FIELD)
			return Fcount;
		else if (kind == STKind.ARG)
			return Arg;
		else
			return Var;

	}

	public STKind kindOf(String name) {
		if (methods.containsKey(name))
			return methods.get(name).getKind();
		if (classes.containsKey(name))
			return classes.get(name).getKind();
		else
			return null;
	}

	public String typeOf(String name) {
		if (methods.containsKey(name))
			return methods.get(name).getType();
		if (classes.containsKey(name))
			return classes.get(name).getType();
		else
			return "";

	}

	public int indexOf(String name) {

		if (methods.containsKey(name))
			return methods.get(name).getIndex();

		if (classes.containsKey(name))
			return classes.get(name).getIndex();

		else
			return -1;

	}

}
