package ao;

/* JADX INFO: loaded from: classes5.dex */
public class n extends zn.n {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f4176b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f4177c;

    public n(String str, int i10, int i11) {
        super(str, null);
        this.f4176b = i10;
        this.f4177c = i11;
    }

    public boolean a() {
        int i10 = this.f4176b;
        return i10 == 3 || i10 == 4 || i10 == 5 || i10 == 6 || i10 == 8;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        StringBuilder sb2 = new StringBuilder(super.getMessage());
        sb2.append(", ErrorCode=");
        sb2.append(this.f4176b);
        int i10 = this.f4177c;
        if (i10 != 0) {
            sb2.append(", InternalErrorCode=");
            sb2.append(i10);
        }
        sb2.append(", Retryable=");
        sb2.append(a());
        return sb2.toString();
    }
}
