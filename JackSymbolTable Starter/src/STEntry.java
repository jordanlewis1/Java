
public class STEntry {

	private String type;
	private STKind kind;
	private int index;
	
	public STEntry(String type, STKind kind, int index){
		this.type = type;
		this.kind = kind;
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public STKind getKind() {
		return kind;
	}

	public int getIndex() {
		return index;
	}
}
