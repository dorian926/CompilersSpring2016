package miniJava.AbstractSyntaxTrees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IdentifierList implements Iterable<Identifier>{
	public IdentifierList() {
		list = new ArrayList<Identifier>();
	}
	public void add(Identifier i) {
		list.add(i);
	}
	public Identifier get(int i) {
		return list.get(i);
	}
	public int size() {
		return list.size();
	}
	@Override
	public Iterator<Identifier> iterator() {
		return list.iterator();
	}
	private List<Identifier> list;
}
