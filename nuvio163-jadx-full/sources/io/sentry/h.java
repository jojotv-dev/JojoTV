package io.sentry;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public final class h extends AbstractCollection implements Queue, Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final transient Object[] f25573b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public transient int f25574c = 0;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public transient int f25575e = 0;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public transient boolean f25576i = false;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final int f25577m;

    public h(int i10) {
        if (i10 <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        Object[] objArr = new Object[i10];
        this.f25573b = objArr;
        this.f25577m = objArr.length;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Queue
    public final boolean add(Object obj) {
        if (obj == null) {
            throw new NullPointerException("Attempted to add null object to queue");
        }
        int size = size();
        int i10 = this.f25577m;
        if (size == i10) {
            remove();
        }
        int i11 = this.f25575e;
        int i12 = i11 + 1;
        this.f25575e = i12;
        this.f25573b[i11] = obj;
        if (i12 >= i10) {
            this.f25575e = 0;
        }
        if (this.f25575e == this.f25574c) {
            this.f25576i = true;
        }
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public final void clear() {
        this.f25576i = false;
        this.f25574c = 0;
        this.f25575e = 0;
        Arrays.fill(this.f25573b, (Object) null);
    }

    @Override // java.util.Queue
    public final Object element() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return peek();
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public final boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public final Iterator iterator() {
        return new g(this);
    }

    @Override // java.util.Queue
    public final boolean offer(Object obj) {
        add(obj);
        return true;
    }

    @Override // java.util.Queue
    public final Object peek() {
        if (isEmpty()) {
            return null;
        }
        return this.f25573b[this.f25574c];
    }

    @Override // java.util.Queue
    public final Object poll() {
        if (isEmpty()) {
            return null;
        }
        return remove();
    }

    @Override // java.util.Queue
    public final Object remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        int i10 = this.f25574c;
        Object[] objArr = this.f25573b;
        Object obj = objArr[i10];
        if (obj != null) {
            int i11 = i10 + 1;
            this.f25574c = i11;
            objArr[i10] = null;
            if (i11 >= this.f25577m) {
                this.f25574c = 0;
            }
            this.f25576i = false;
        }
        return obj;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public final int size() {
        int i10 = this.f25575e;
        int i11 = this.f25574c;
        int i12 = this.f25577m;
        if (i10 < i11) {
            return (i12 - i11) + i10;
        }
        if (i10 != i11) {
            return i10 - i11;
        }
        if (this.f25576i) {
            return i12;
        }
        return 0;
    }
}
