import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class NestedInterface {

	public static void main(String[] args) {
		doDummyWork();
	} 

	public static void doDummyWork() {
		final int nbEls = 1<<25;
		final int nbRuns = 1<<0;
		final int nbComputations = 1<<6;
		
		runList2(nbEls, nbRuns, nbComputations);
		
	}
	
	public static void runList2(final int nbEls, final int nbRuns, final int nbComputations) {
		for(int run=0; run<nbRuns; run++) {
			final ArrayList<Integer> sourceList = new ArrayList<>();

			for (int i=0; i<nbEls; ++i) {
				sourceList.add(i+1);
			}
		}
	}
	

	public interface IList {
		Object read(int idx);

		int readInt(int idx);

		long readLong(int idx);
	}

	public interface IWritableList extends IList {
		void write(int idx, Object value);	

		void add(Object value);

		void writeInt(int idx, int value);	

		void addInt(int value);

		void writeLong(int idx, long value);	

		void addLong(long value);
	}

	protected static interface INumberList<T extends Number> extends IList, Iterable<T>{
		int size();
	}

	public static interface IWritableNumberList<T extends Number> extends INumberList<T>, IWritableList {
		void ensureCapacity(int capacity);

		void clear();
	}


	protected abstract static class ANumberList<T extends Number> implements INumberList<T> {

		final List<T> underlying;

		long counter = 0;

		public ANumberList(List<T> underlying) {
			this.underlying = underlying;
		}

		public int size() {
			return underlying.size();
		}

	}

	protected static class List1 extends ANumberList<Integer> implements IWritableNumberList<Integer> {

		public List1() {
			super(new Vector<Integer>());
		}

		@Override
		public Object read(int idx) {
			return underlying.get(idx);
		}

		@Override
		public int readInt(int idx) {
			return underlying.get(idx);
		}

		@Override
		public long readLong(int idx) {
			return underlying.get(idx);
		}

		@Override
		public Iterator<Integer> iterator() {
			return underlying.iterator();
		}

		@Override
		public void write(int idx, Object value) {
		}

		@Override
		public void add(Object value) {
		}

		@Override
		public void writeInt(int idx, int value) {
		}

		@Override
		public void addInt(int value) {
		}

		@Override
		public void writeLong(int idx, long value) {
		}

		@Override
		public void addLong(long value) {
		}

		@Override
		public void ensureCapacity(int capacity) {
		}

		@Override
		public void clear() {
			underlying.clear();
		}

	}


	protected static interface IDummyTable2 extends IList{
		int dummyTable2();
	}

	protected static interface IList2 extends IDummyTable2, IWritableList {
		int list2();
	}

	protected static class List2 extends ANumberList<Integer> implements IWritableNumberList<Integer>, IList2{

		public List2() {
			super(new ArrayList<Integer>());
		}

		@Override
		public Object read(int idx) {
			return underlying.get(idx);
		}

		@Override
		public int readInt(int idx) {
			return underlying.get(idx);
		}

		@Override
		public long readLong(int idx) {
			return underlying.get(idx);
		}

		@Override
		public Iterator<Integer> iterator() {
			return underlying.iterator();
		}

		@Override
		public void write(int idx, Object value) {
		}

		@Override
		public void add(Object value) {
			underlying.add((Integer)value);
		}

		@Override
		public void writeInt(int idx, int value) {
		}

		@Override
		public void addInt(int value) {
		}

		@Override
		public void writeLong(int idx, long value) {
		}

		@Override
		public void addLong(long value) {
		}

		@Override
		public void ensureCapacity(int capacity) {
		}

		@Override
		public void clear() {
		}

		@Override
		public int dummyTable2() {
			return 0;
		}

		@Override
		public int list2() {
			return 0;
		}

	}

}
