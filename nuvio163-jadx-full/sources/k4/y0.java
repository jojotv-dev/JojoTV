package k4;

import android.view.View;
import android.view.ViewGroup;
import java.util.Iterator;
import kotlin.jvm.internal.markers.KMutableIterator;

/* JADX INFO: loaded from: classes3.dex */
public final class y0 implements Iterator, KMutableIterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f27863b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ ViewGroup f27864c;

    public y0(ViewGroup viewGroup) {
        this.f27864c = viewGroup;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f27863b < this.f27864c.getChildCount();
    }

    @Override // java.util.Iterator
    public final Object next() {
        int i10 = this.f27863b;
        this.f27863b = i10 + 1;
        View childAt = this.f27864c.getChildAt(i10);
        if (childAt != null) {
            return childAt;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // java.util.Iterator
    public final void remove() {
        int i10 = this.f27863b - 1;
        this.f27863b = i10;
        this.f27864c.removeViewAt(i10);
    }
}
