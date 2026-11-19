package ph;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

/* JADX INFO: loaded from: classes5.dex */
public final class b implements lh.b {

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final int f45069q = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096).intValue();

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final Object f45070r = new Object();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final AtomicLong f45071b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f45072c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f45073e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int f45074i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public AtomicReferenceArray f45075m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final int f45076n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public AtomicReferenceArray f45077o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final AtomicLong f45078p;

    public b(int i10) {
        AtomicLong atomicLong = new AtomicLong();
        this.f45071b = atomicLong;
        this.f45078p = new AtomicLong();
        int iNumberOfLeadingZeros = 1 << (32 - Integer.numberOfLeadingZeros(Math.max(8, i10) - 1));
        int i11 = iNumberOfLeadingZeros - 1;
        AtomicReferenceArray atomicReferenceArray = new AtomicReferenceArray(iNumberOfLeadingZeros + 1);
        this.f45075m = atomicReferenceArray;
        this.f45074i = i11;
        this.f45072c = Math.min(iNumberOfLeadingZeros / 4, f45069q);
        this.f45077o = atomicReferenceArray;
        this.f45076n = i11;
        this.f45073e = iNumberOfLeadingZeros - 2;
        atomicLong.lazySet(0L);
    }

    @Override // lh.b
    public final void clear() {
        while (true) {
            if (poll() == null && isEmpty()) {
                return;
            }
        }
    }

    @Override // lh.b
    public final boolean isEmpty() {
        return this.f45071b.get() == this.f45078p.get();
    }

    @Override // lh.b
    public final boolean offer(Object obj) {
        if (obj == null) {
            throw new NullPointerException("Null is not a valid element");
        }
        AtomicReferenceArray atomicReferenceArray = this.f45075m;
        AtomicLong atomicLong = this.f45071b;
        long j10 = atomicLong.get();
        int i10 = this.f45074i;
        int i11 = ((int) j10) & i10;
        if (j10 < this.f45073e) {
            atomicReferenceArray.lazySet(i11, obj);
            atomicLong.lazySet(j10 + 1);
            return true;
        }
        long j11 = ((long) this.f45072c) + j10;
        if (atomicReferenceArray.get(((int) j11) & i10) == null) {
            this.f45073e = j11 - 1;
            atomicReferenceArray.lazySet(i11, obj);
            atomicLong.lazySet(j10 + 1);
            return true;
        }
        long j12 = j10 + 1;
        if (atomicReferenceArray.get(((int) j12) & i10) == null) {
            atomicReferenceArray.lazySet(i11, obj);
            atomicLong.lazySet(j12);
            return true;
        }
        AtomicReferenceArray atomicReferenceArray2 = new AtomicReferenceArray(atomicReferenceArray.length());
        this.f45075m = atomicReferenceArray2;
        this.f45073e = (j10 + ((long) i10)) - 1;
        atomicReferenceArray2.lazySet(i11, obj);
        atomicReferenceArray.lazySet(atomicReferenceArray.length() - 1, atomicReferenceArray2);
        atomicReferenceArray.lazySet(i11, f45070r);
        atomicLong.lazySet(j12);
        return true;
    }

    @Override // lh.b
    public final Object poll() {
        AtomicReferenceArray atomicReferenceArray = this.f45077o;
        AtomicLong atomicLong = this.f45078p;
        long j10 = atomicLong.get();
        int i10 = this.f45076n;
        int i11 = ((int) j10) & i10;
        Object obj = atomicReferenceArray.get(i11);
        boolean z2 = obj == f45070r;
        if (obj != null && !z2) {
            atomicReferenceArray.lazySet(i11, null);
            atomicLong.lazySet(j10 + 1);
            return obj;
        }
        if (!z2) {
            return null;
        }
        int i12 = i10 + 1;
        AtomicReferenceArray atomicReferenceArray2 = (AtomicReferenceArray) atomicReferenceArray.get(i12);
        atomicReferenceArray.lazySet(i12, null);
        this.f45077o = atomicReferenceArray2;
        Object obj2 = atomicReferenceArray2.get(i11);
        if (obj2 != null) {
            atomicReferenceArray2.lazySet(i11, null);
            atomicLong.lazySet(j10 + 1);
        }
        return obj2;
    }
}
