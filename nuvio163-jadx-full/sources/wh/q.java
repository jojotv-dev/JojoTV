package wh;

/* JADX INFO: loaded from: classes5.dex */
public final class q extends p {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f62579i;

    @Override // java.util.Iterator
    public final Object next() {
        switch (this.f62579i) {
            case 0:
                int i10 = this.f62578e;
                this.f62578e = i10 + 2;
                Object[] objArr = this.f62576b;
                return new a(objArr[i10], objArr[i10 + 1]);
            case 1:
                int i11 = this.f62578e;
                this.f62578e = i11 + 2;
                return this.f62576b[i11];
            default:
                int i12 = this.f62578e;
                this.f62578e = i12 + 2;
                return this.f62576b[i12 + 1];
        }
    }
}
