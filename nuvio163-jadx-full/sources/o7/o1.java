package o7;

/* JADX INFO: loaded from: classes3.dex */
public final class o1 extends IllegalArgumentException {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final String f43643i = System.getProperty("line.separator");

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f43644b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f43645c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f43646e;

    public o1(String str, String str2, int i10) {
        this.f43644b = str;
        this.f43645c = str2;
        this.f43646e = i10;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.f43644b);
        int i10 = this.f43646e;
        if (i10 >= 0) {
            stringBuffer.append(" near index ");
            stringBuffer.append(i10);
        }
        String str = f43643i;
        stringBuffer.append(str);
        stringBuffer.append(this.f43645c);
        if (i10 >= 0) {
            stringBuffer.append(str);
            for (int i11 = 0; i11 < i10; i11++) {
                stringBuffer.append(' ');
            }
            stringBuffer.append('^');
        }
        return stringBuffer.toString();
    }
}
