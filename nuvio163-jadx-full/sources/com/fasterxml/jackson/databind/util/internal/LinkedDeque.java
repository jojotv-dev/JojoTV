package com.fasterxml.jackson.databind.util.internal;

import com.fasterxml.jackson.databind.util.internal.Linked;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes3.dex */
final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {
    E first;
    E last;

    public abstract class AbstractLinkedIterator implements Iterator<E> {
        E cursor;

        public AbstractLinkedIterator(E e10) {
            this.cursor = e10;
        }

        public abstract E computeNext();

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor != null;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E e10 = this.cursor;
            this.cursor = (E) computeNext();
            return e10;
        }
    }

    public void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        Linked linked = this.first;
        while (linked != null) {
            Linked next = linked.getNext();
            linked.setPrevious(null);
            linked.setNext(null);
            linked = next;
        }
        this.last = null;
        this.first = null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public boolean contains(Object obj) {
        return (obj instanceof Linked) && contains((Linked<?>) obj);
    }

    @Override // java.util.Deque
    public Iterator<E> descendingIterator() {
        return new LinkedDeque<E>.AbstractLinkedIterator(this.last) { // from class: com.fasterxml.jackson.databind.util.internal.LinkedDeque.2
            @Override // com.fasterxml.jackson.databind.util.internal.LinkedDeque.AbstractLinkedIterator
            public E computeNext() {
                return (E) this.cursor.getPrevious();
            }
        };
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return this.first == null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Deque
    public Iterator<E> iterator() {
        return new LinkedDeque<E>.AbstractLinkedIterator(this.first) { // from class: com.fasterxml.jackson.databind.util.internal.LinkedDeque.1
            @Override // com.fasterxml.jackson.databind.util.internal.LinkedDeque.AbstractLinkedIterator
            public E computeNext() {
                return (E) this.cursor.getNext();
            }
        };
    }

    public void linkFirst(E e10) {
        E e11 = this.first;
        this.first = e10;
        if (e11 == null) {
            this.last = e10;
        } else {
            e11.setPrevious(e10);
            e10.setNext(e11);
        }
    }

    public void linkLast(E e10) {
        E e11 = this.last;
        this.last = e10;
        if (e11 == null) {
            this.first = e10;
        } else {
            e11.setNext(e10);
            e10.setPrevious(e11);
        }
    }

    public void moveToBack(E e10) {
        if (e10 != this.last) {
            unlink(e10);
            linkLast(e10);
        }
    }

    public void moveToFront(E e10) {
        if (e10 != this.first) {
            unlink(e10);
            linkFirst(e10);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        boolean zRemove = false;
        while (it.hasNext()) {
            zRemove |= remove(it.next());
        }
        return zRemove;
    }

    @Override // java.util.Deque
    public boolean removeFirstOccurrence(Object obj) {
        return remove(obj);
    }

    @Override // java.util.Deque
    public boolean removeLastOccurrence(Object obj) {
        return remove(obj);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public int size() {
        int i10 = 0;
        for (Linked next = this.first; next != null; next = next.getNext()) {
            i10++;
        }
        return i10;
    }

    public void unlink(E e10) {
        E e11 = (E) e10.getPrevious();
        E e12 = (E) e10.getNext();
        if (e11 == null) {
            this.first = e12;
        } else {
            e11.setNext(e12);
            e10.setPrevious(null);
        }
        if (e12 == null) {
            this.last = e11;
        } else {
            e12.setPrevious(e11);
            e10.setNext(null);
        }
    }

    public E unlinkFirst() {
        E e10 = this.first;
        E e11 = (E) e10.getNext();
        e10.setNext(null);
        this.first = e11;
        if (e11 == null) {
            this.last = null;
            return e10;
        }
        e11.setPrevious(null);
        return e10;
    }

    public E unlinkLast() {
        E e10 = this.last;
        E e11 = (E) e10.getPrevious();
        e10.setPrevious(null);
        this.last = e11;
        if (e11 == null) {
            this.first = null;
            return e10;
        }
        e11.setNext(null);
        return e10;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque, java.util.Queue
    public boolean add(E e10) {
        return offerLast((Linked) e10);
    }

    @Override // java.util.Deque
    public void addFirst(E e10) {
        if (!offerFirst((Linked) e10)) {
            throw new IllegalArgumentException();
        }
    }

    @Override // java.util.Deque
    public void addLast(E e10) {
        if (!offerLast((Linked) e10)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean contains(Linked<?> linked) {
        return (linked.getPrevious() == null && linked.getNext() == null && linked != this.first) ? false : true;
    }

    @Override // java.util.Deque, java.util.Queue
    public E element() {
        return (E) getFirst();
    }

    @Override // java.util.Deque
    public E getFirst() {
        checkNotEmpty();
        return (E) peekFirst();
    }

    @Override // java.util.Deque
    public E getLast() {
        checkNotEmpty();
        return (E) peekLast();
    }

    @Override // java.util.Deque, java.util.Queue
    public boolean offer(E e10) {
        return offerLast((Linked) e10);
    }

    @Override // java.util.Deque
    public boolean offerFirst(E e10) {
        if (contains((Linked<?>) e10)) {
            return false;
        }
        linkFirst(e10);
        return true;
    }

    @Override // java.util.Deque
    public boolean offerLast(E e10) {
        if (contains((Linked<?>) e10)) {
            return false;
        }
        linkLast(e10);
        return true;
    }

    @Override // java.util.Deque, java.util.Queue
    public E peek() {
        return (E) peekFirst();
    }

    @Override // java.util.Deque
    public E peekFirst() {
        return this.first;
    }

    @Override // java.util.Deque
    public E peekLast() {
        return this.last;
    }

    @Override // java.util.Deque, java.util.Queue
    public E poll() {
        return (E) pollFirst();
    }

    @Override // java.util.Deque
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        return (E) unlinkFirst();
    }

    @Override // java.util.Deque
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        return (E) unlinkLast();
    }

    @Override // java.util.Deque
    public E pop() {
        return (E) removeFirst();
    }

    @Override // java.util.Deque
    public void push(E e10) {
        addFirst((Linked) e10);
    }

    @Override // java.util.Deque, java.util.Queue
    public E remove() {
        return (E) removeFirst();
    }

    @Override // java.util.Deque
    public E removeFirst() {
        checkNotEmpty();
        return (E) pollFirst();
    }

    @Override // java.util.Deque
    public E removeLast() {
        checkNotEmpty();
        return (E) pollLast();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public boolean remove(Object obj) {
        return (obj instanceof Linked) && remove((Linked) obj);
    }

    public boolean remove(E e10) {
        if (!contains((Linked<?>) e10)) {
            return false;
        }
        unlink(e10);
        return true;
    }
}
