package n1;

/* JADX INFO: loaded from: classes.dex */
public final class p extends o {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f39451i;

    @Override // java.util.Iterator
    public final Object next() {
        switch (this.f39451i) {
            case 0:
                int i10 = this.f39450e;
                this.f39450e = i10 + 2;
                Object[] objArr = this.f39448b;
                return new a(objArr[i10], objArr[i10 + 1], 0);
            case 1:
                int i11 = this.f39450e;
                this.f39450e = i11 + 2;
                return this.f39448b[i11];
            default:
                int i12 = this.f39450e;
                this.f39450e = i12 + 2;
                return this.f39448b[i12 + 1];
        }
    }
}
