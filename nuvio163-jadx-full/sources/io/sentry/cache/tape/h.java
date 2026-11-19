package io.sentry.cache.tape;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes5.dex */
public final class h implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f25429b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f25430c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f25431e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ i f25432i;

    public h(i iVar) {
        this.f25432i = iVar;
        this.f25430c = iVar.f25438m.f25427a;
        this.f25431e = iVar.f25441p;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        i iVar = this.f25432i;
        if (iVar.f25443r) {
            throw new IllegalStateException("closed");
        }
        if (iVar.f25441p == this.f25431e) {
            return this.f25429b != iVar.f25437i;
        }
        throw new ConcurrentModificationException();
    }

    @Override // java.util.Iterator
    public final Object next() throws IOException {
        byte[] bArr = i.f25433s;
        i iVar = this.f25432i;
        if (iVar.f25443r) {
            throw new IllegalStateException("closed");
        }
        if (iVar.f25441p != this.f25431e) {
            throw new ConcurrentModificationException();
        }
        int i10 = iVar.f25437i;
        if (i10 == 0) {
            throw new NoSuchElementException();
        }
        if (this.f25429b >= i10) {
            throw new NoSuchElementException();
        }
        try {
            g gVarU = iVar.U(this.f25430c);
            int i11 = gVarU.f25428b;
            long j10 = gVarU.f25427a;
            byte[] bArr2 = new byte[i11];
            long j11 = j10 + 4;
            long jD0 = iVar.d0(j11);
            this.f25430c = jD0;
            if (!iVar.b0(jD0, bArr2, i11)) {
                this.f25429b = iVar.f25437i;
                return bArr;
            }
            this.f25430c = iVar.d0(j11 + ((long) i11));
            this.f25429b++;
            return bArr2;
        } catch (IOException e10) {
            throw e10;
        } catch (OutOfMemoryError unused) {
            iVar.a0();
            this.f25429b = iVar.f25437i;
            return bArr;
        }
    }

    @Override // java.util.Iterator
    public final void remove() {
        i iVar = this.f25432i;
        if (iVar.f25441p != this.f25431e) {
            throw new ConcurrentModificationException();
        }
        if (iVar.f25437i == 0) {
            throw new NoSuchElementException();
        }
        if (this.f25429b != 1) {
            throw new UnsupportedOperationException("Removal is only permitted from the head.");
        }
        iVar.Z(1);
        this.f25431e = iVar.f25441p;
        this.f25429b--;
    }
}
