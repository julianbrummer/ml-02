package uni.ml.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A view on a subset of a dataset. 
 * A dataset can be decorated with multiple views.
 * For example a dataset can be decorated with an {@link DatasetIndexedView} to select instances, 
 * which itself can be decorated with a {@link DatasetPredicateView} to filter out instances according to some predicate.
 * 
 * @author Julian Brummer
 *
 */
public abstract class DatasetView {
	
	private interface ListIteratorBase<T> extends ListIterator<T>, Iterable<T> {

		int index = 0;

		@Override
		public default boolean hasPrevious() {
			return index-1 >= 0;
		}

		@Override
		public default int nextIndex() {
			return index;
		}

		@Override
		public default int previousIndex() {
			return index-1;
		}

		@Override
		public default void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void set(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void add(T e) {
			throw new UnsupportedOperationException();			
		}
		
		@Override
		public default Iterator<T> iterator() {
			return this;
		}
		
	}
	
	private class AttributeIterator implements ListIteratorBase<EnumAttribute<?>> {

		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < numAttributes();
		}

		@Override
		public EnumAttribute<?> next() {
			if (index >= numAttributes())
				throw new NoSuchElementException();
			return attributeAt(index++);
		}

		@Override
		public EnumAttribute<?> previous() {
			return attributeAt(--index);
		}
		
	}
	
	private class InstanceIterator implements ListIteratorBase<Instance> {

		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < numInstances();
		}

		@Override
		public Instance next() {
			if (index >= numInstances())
				throw new NoSuchElementException();
			return instanceAt(index++);
		}

		@Override
		public Instance previous() {
			return instanceAt(--index);
		}
		
	}
	
	
	public abstract int numAttributes();
	public abstract int numInstances();
	public abstract EnumAttribute<?> attributeAt(int index);
	public abstract Instance instanceAt(int index);
	
	public Iterable<EnumAttribute<?>> attributes() {
		return new AttributeIterator();
	}

	public Iterable<Instance> instances() {
		return new InstanceIterator();
	}
	
	public boolean hasAttributes() {
		return numAttributes() > 0;
	}
	
	public boolean hasInstances() {
		return numInstances() > 0;
	}
	
	@Override
	public String toString() {
		// get list of attributes visible in view
		List<Attribute<?>> attributes = new ArrayList<>();
		for (int i = 0; i < numAttributes(); i++) {
			attributes.add(attributeAt(i));
		}
		
		// append attribute list
		StringBuilder b = new StringBuilder();
		if (hasAttributes()) {
			for (int i=0; i< numAttributes()-1; i++) {
				b.append(attributes.get(i)).append(",");
			}
			b.append(attributes.get(numAttributes()-1));
		}
		
		b.append("\n");
		
		// append instance list
		if (hasInstances()) {
			for (int i=0; i< numInstances()-1; i++) {
				b.append(instanceAt(i).toString(attributes)).append("\n");
			}
			b.append(instanceAt(numInstances()-1).toString(attributes));
		}
		return b.toString();
	}
	
	
	
}
