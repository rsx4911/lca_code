package com.greendelta.collaboration.util;

import java.util.Iterator;
import java.util.function.Function;

public class WrappedIterator<V, T> implements Iterator<T> {

	private Function<V, T> unwrap;
	private Iterator<V> iterator;
	
	public WrappedIterator(Iterator<V> iterator, Function<V, T> unwrap) {
		this.iterator = iterator;
		this.unwrap = unwrap;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		V next = iterator.next();
		return unwrap.apply(next);
	}

}
