package io.sentry;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public final class g implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f25553b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f25554c = -1;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f25555e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ h f25556i;

    public g(h hVar) {
        this.f25556i = hVar;
        this.f25553b = hVar.f25574c;
        this.f25555e = hVar.f25576i;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f25555e || this.f25553b != this.f25556i.f25575e;
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.f25555e = false;
        int i10 = this.f25553b;
        this.f25554c = i10;
        int i11 = i10 + 1;
        h hVar = this.f25556i;
        this.f25553b = i11 < hVar.f25577m ? i11 : 0;
        return hVar.f25573b[i10];
    }

    @Override // java.util.Iterator
    public final void remove() {
        int i10;
        h hVar = this.f25556i;
        int i11 = hVar.f25577m;
        Object[] objArr = hVar.f25573b;
        int i12 = this.f25554c;
        if (i12 == -1) {
            throw new IllegalStateException();
        }
        int i13 = hVar.f25574c;
        if (i12 == i13) {
            hVar.remove();
            this.f25554c = -1;
            return;
        }
        int i14 = i12 + 1;
        if (i13 >= i12 || i14 >= (i10 = hVar.f25575e)) {
            while (i14 != hVar.f25575e) {
                if (i14 >= i11) {
                    objArr[i14 - 1] = objArr[0];
                } else {
                    int i15 = i14 - 1;
                    if (i15 < 0) {
                        i15 = i11 - 1;
                    }
                    objArr[i15] = objArr[i14];
                    i14++;
                    if (i14 >= i11) {
                    }
                }
                i14 = 0;
            }
        } else {
            System.arraycopy(objArr, i14, objArr, i12, i10 - i14);
        }
        this.f25554c = -1;
        int i16 = hVar.f25575e - 1;
        if (i16 < 0) {
            i16 = i11 - 1;
        }
        hVar.f25575e = i16;
        objArr[i16] = null;
        hVar.f25576i = false;
        int i17 = this.f25553b - 1;
        if (i17 < 0) {
            i17 = i11 - 1;
        }
        this.f25553b = i17;
    }
}
