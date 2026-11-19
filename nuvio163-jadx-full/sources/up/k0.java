package up;

import java.util.Random;

/* JADX INFO: loaded from: classes5.dex */
public final class k0 implements Cloneable {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final Random f60181i = new Random();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f60182b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f60183c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int[] f60184e;

    public k0(int i10) {
        this.f60184e = new int[4];
        this.f60183c = 0;
        this.f60182b = -1;
        if (i10 >= 0 && i10 <= 65535) {
            this.f60182b = i10;
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("DNS message ID ");
        stringBuffer.append(i10);
        stringBuffer.append(" is out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public static void a(int i10) {
        if (i10 >= 0 && i10 <= 15) {
            f0.f60125a.b(i10);
            if ((i10 < 1 || i10 > 4) && i10 < 12) {
                return;
            }
        }
        StringBuffer stringBuffer = new StringBuffer("invalid flag bit ");
        stringBuffer.append(i10);
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public final boolean b(int i10) {
        a(i10);
        return ((1 << (15 - i10)) & this.f60183c) != 0;
    }

    public final int c() {
        int i10;
        int i11 = this.f60182b;
        if (i11 >= 0) {
            return i11;
        }
        synchronized (this) {
            try {
                if (this.f60182b < 0) {
                    this.f60182b = f60181i.nextInt(65535);
                }
                i10 = this.f60182b;
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return i10;
    }

    public final Object clone() {
        k0 k0Var = new k0();
        k0Var.f60182b = this.f60182b;
        k0Var.f60183c = this.f60183c;
        int[] iArr = k0Var.f60184e;
        int[] iArr2 = this.f60184e;
        System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
        return k0Var;
    }

    public final void d() {
        this.f60183c &= 34815;
    }

    public final String e(int i10) {
        StringBuffer stringBuffer = new StringBuffer(";; ->>HEADER<<- ");
        StringBuffer stringBuffer2 = new StringBuffer("opcode: ");
        stringBuffer2.append(q1.f60239a.c((this.f60183c >> 11) & 15));
        stringBuffer.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer(", status: ");
        stringBuffer3.append(y1.f60315a.c(i10));
        stringBuffer.append(stringBuffer3.toString());
        StringBuffer stringBuffer4 = new StringBuffer(", id: ");
        stringBuffer4.append(c());
        stringBuffer.append(stringBuffer4.toString());
        stringBuffer.append("\n");
        StringBuffer stringBuffer5 = new StringBuffer(";; flags: ");
        StringBuffer stringBuffer6 = new StringBuffer();
        for (int i11 = 0; i11 < 16; i11++) {
            if (i11 >= 0 && i11 <= 15) {
                b1 b1Var = f0.f60125a;
                b1Var.b(i11);
                if ((i11 < 1 || i11 > 4) && i11 < 12 && b(i11)) {
                    stringBuffer6.append(b1Var.c(i11));
                    stringBuffer6.append(" ");
                }
            }
        }
        stringBuffer5.append(stringBuffer6.toString());
        stringBuffer.append(stringBuffer5.toString());
        stringBuffer.append("; ");
        for (int i12 = 0; i12 < 4; i12++) {
            StringBuffer stringBuffer7 = new StringBuffer();
            stringBuffer7.append(l2.f60198a.c(i12));
            stringBuffer7.append(": ");
            stringBuffer7.append(this.f60184e[i12]);
            stringBuffer7.append(" ");
            stringBuffer.append(stringBuffer7.toString());
        }
        return stringBuffer.toString();
    }

    public final String toString() {
        return e(this.f60183c & 15);
    }

    public k0() {
        this.f60184e = new int[4];
        this.f60183c = 0;
        this.f60182b = -1;
    }
}
