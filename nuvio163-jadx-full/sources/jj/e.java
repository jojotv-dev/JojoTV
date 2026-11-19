package jj;

/* JADX INFO: loaded from: classes5.dex */
public final class e extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f27544b;

    public e(int i10, int i11, Object obj) {
        super(a(i10, i11, obj));
        this.f27544b = obj;
    }

    public static String a(int i10, int i11, Object obj) {
        StringBuilder sb2 = new StringBuilder();
        if (i11 == 0) {
            sb2.append("Unexpected character (");
            sb2.append(obj);
            sb2.append(") at position ");
            sb2.append(i10);
            sb2.append(".");
        } else if (i11 == 1) {
            sb2.append("Unexpected token ");
            sb2.append(obj);
            sb2.append(" at position ");
            sb2.append(i10);
            sb2.append(".");
        } else if (i11 == 2) {
            sb2.append("Unexpected exception ");
            sb2.append(obj);
            sb2.append(" occur at position ");
            sb2.append(i10);
            sb2.append(".");
        } else if (i11 == 3) {
            sb2.append("Unexpected End Of File position ");
            sb2.append(i10);
            sb2.append(": ");
            sb2.append(obj);
        } else if (i11 == 4) {
            sb2.append("Unexpected unicode escape sequence ");
            sb2.append(obj);
            sb2.append(" at position ");
            sb2.append(i10);
            sb2.append(".");
        } else if (i11 == 5) {
            sb2.append("Unexpected duplicate key:");
            sb2.append(obj);
            sb2.append(" at position ");
            sb2.append(i10);
            sb2.append(".");
        } else if (i11 == 6) {
            sb2.append("Unexpected leading 0 in digit for token:");
            sb2.append(obj);
            sb2.append(" at position ");
            sb2.append(i10);
            sb2.append(".");
        } else {
            sb2.append("Unkown error at position ");
            sb2.append(i10);
            sb2.append(".");
        }
        return sb2.toString();
    }
}
