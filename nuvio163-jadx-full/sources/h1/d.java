package h1;

import java.util.List;
import java.util.ListIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableListIterator;

/* JADX INFO: loaded from: classes.dex */
public final class d implements ListIterator, KMutableListIterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f20583b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f20584c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f20585e;

    public d(int i10, int i11, List list) {
        this.f20583b = i11;
        switch (i11) {
            case 1:
                Intrinsics.checkNotNullParameter(list, "list");
                this.f20584c = list;
                this.f20585e = i10 - 1;
                break;
            default:
                this.f20584c = list;
                this.f20585e = i10;
                break;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator
    public final void add(Object obj) {
        switch (this.f20583b) {
            case 0:
                this.f20584c.add(this.f20585e, obj);
                this.f20585e++;
                break;
            default:
                int i10 = this.f20585e + 1;
                this.f20585e = i10;
                this.f20584c.add(i10, obj);
                break;
        }
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator, java.util.Iterator
    public final boolean hasNext() {
        switch (this.f20583b) {
            case 0:
                return this.f20585e < this.f20584c.size();
            default:
                return this.f20585e < this.f20584c.size() - 1;
        }
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        switch (this.f20583b) {
            case 0:
                return this.f20585e > 0;
            default:
                return this.f20585e >= 0;
        }
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator, java.util.Iterator
    public final Object next() {
        switch (this.f20583b) {
            case 0:
                int i10 = this.f20585e;
                this.f20585e = i10 + 1;
                return this.f20584c.get(i10);
            default:
                int i11 = this.f20585e + 1;
                this.f20585e = i11;
                return this.f20584c.get(i11);
        }
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        switch (this.f20583b) {
            case 0:
                return this.f20585e;
            default:
                return this.f20585e + 1;
        }
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator
    public final Object previous() {
        switch (this.f20583b) {
            case 0:
                int i10 = this.f20585e - 1;
                this.f20585e = i10;
                return this.f20584c.get(i10);
            default:
                int i11 = this.f20585e;
                this.f20585e = i11 - 1;
                return this.f20584c.get(i11);
        }
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        switch (this.f20583b) {
            case 0:
                return this.f20585e - 1;
            default:
                return this.f20585e;
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator, java.util.Iterator
    public final void remove() {
        switch (this.f20583b) {
            case 0:
                int i10 = this.f20585e - 1;
                this.f20585e = i10;
                this.f20584c.remove(i10);
                break;
            default:
                this.f20584c.remove(this.f20585e);
                this.f20585e--;
                break;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, java.util.List] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, java.util.List] */
    @Override // java.util.ListIterator
    public final void set(Object obj) {
        switch (this.f20583b) {
            case 0:
                this.f20584c.set(this.f20585e, obj);
                break;
            default:
                this.f20584c.set(this.f20585e, obj);
                break;
        }
    }
}
