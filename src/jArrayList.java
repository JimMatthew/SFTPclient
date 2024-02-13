import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


//my attempt at implementing a basic arrayList
//
public class jArrayList<E> extends AbstractList<E> implements List<E> {

    private Object[] objectArray;
    private int numItems;

    public jArrayList() {
        this(10);
    }

    public jArrayList(int size) {
        objectArray = new Object[size];
        numItems = 0;
    }

    @Override
    public int size() {
        return numItems;
    }

    @Override
    public boolean isEmpty() {
        return (size() == 0);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < numItems; i++) {
            if (objectArray[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator iterator() {
        return super.iterator();
    }

    @Override
    public Object[] toArray() {
        shrink();
        return objectArray.clone();
    }

    @Override
    public Object[] toArray(Object[] a) {
        shrink();
        if (a.length >= numItems) {
            if (numItems >= 0) System.arraycopy(objectArray, 0, a, 0, numItems);
        }
        return a;
    }

    @Override
    public boolean add(Object e) {
        ensureCapacity(numItems + 1);
        objectArray[numItems] = e;
        numItems++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            remove(index);
            numItems--;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        int size = c.size();
        int count = 0;
        ensureCapacity(numItems + size);
        for (int i = numItems; i < numItems + size; i++) {
            objectArray[i] = ((List<E>) c).get(count);
            count++;
        }
        numItems = numItems + size;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        objectArray = new Object[10];
        numItems = 0;
    }

    @Override
    public E get(int index) {
        return (E) objectArray[index];
    }

    @Override
    public Object set(int index, Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void add(int index, Object element) {
        // TODO Auto-generated method stub

    }

    @Override
    public E remove(int index) {
        Object o = objectArray[index];
        for (int i = index; i < numItems; i++) {
            objectArray[i] = objectArray[i + 1];
        }
        numItems--;
        return (E) o;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < numItems; i++) {
            if (objectArray[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = numItems - 1; i >= 0; i--) {
            if (objectArray[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    private void ensureCapacity(int size) {
        if (objectArray.length < size) {
            Object[] newObjectArray = new Object[size];
            if (numItems >= 0) System.arraycopy(objectArray, 0, newObjectArray, 0, numItems);
            objectArray = newObjectArray;
        }
    }

    private void shrink() {
        Object[] newObjectArray = new Object[numItems];
        System.arraycopy(objectArray, 0, newObjectArray, 0, numItems);
        objectArray = newObjectArray;
    }

}
