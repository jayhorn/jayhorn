package soottocfg.spec;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListSimpleSpec<E> implements List<E> {

	private int fakeSize = 0;
	
	public ListSimpleSpec(ListSimpleSpec<? extends E> other) {
		fakeSize = other.size();
	}
	
	public ListSimpleSpec() {		
	}

	@Override
	public int size() {
		return fakeSize;
	}

	@Override
	public boolean isEmpty() {
		return fakeSize==0;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(E e) {
		fakeSize++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		assert fakeSize>0;
		fakeSize--;
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		fakeSize+=c.size();
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		fakeSize+=c.size();
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		fakeSize=0;
	}


	@Override
	public void add(int index, E element) {
		assert index>=0; 
		assert index<fakeSize;
		fakeSize++;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E get(int index) {
		assert index>=0; 
		assert index<fakeSize;
		return null;
	}

	@Override
	public E set(int index, E element) {
		assert index>=0; 
		assert index<fakeSize;
		return null;
	}

	@Override
	public E remove(int index) {
		assert index>=0; 
		assert index<fakeSize;
		fakeSize--;
		return null;
	}

}
