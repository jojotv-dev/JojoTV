package w4;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public final class x {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final ThreadLocal f62027d = new ThreadLocal();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f62028a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final g5.e f62029b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile int f62030c = 0;

    public x(g5.e eVar, int i10) {
        this.f62029b = eVar;
        this.f62028a = i10;
    }

    public final int a(int i10) {
        x4.a aVarB = b();
        int iA = aVarB.a(16);
        if (iA == 0) {
            return 0;
        }
        ByteBuffer byteBuffer = aVarB.f63425b;
        int i11 = iA + aVarB.f63424a;
        return byteBuffer.getInt((i10 * 4) + byteBuffer.getInt(i11) + i11 + 4);
    }

    public final x4.a b() {
        ThreadLocal threadLocal = f62027d;
        x4.a aVar = (x4.a) threadLocal.get();
        if (aVar == null) {
            aVar = new x4.a();
            threadLocal.set(aVar);
        }
        x4.b bVar = (x4.b) this.f62029b.f18806b;
        int iA = bVar.a(6);
        if (iA != 0) {
            int i10 = iA + bVar.f63424a;
            int i11 = (this.f62028a * 4) + bVar.f63425b.getInt(i10) + i10 + 4;
            int i12 = bVar.f63425b.getInt(i11) + i11;
            ByteBuffer byteBuffer = bVar.f63425b;
            aVar.f63425b = byteBuffer;
            if (byteBuffer != null) {
                aVar.f63424a = i12;
                int i13 = i12 - byteBuffer.getInt(i12);
                aVar.f63426c = i13;
                aVar.f63427d = aVar.f63425b.getShort(i13);
                return aVar;
            }
            aVar.f63424a = 0;
            aVar.f63426c = 0;
            aVar.f63427d = 0;
        }
        return aVar;
    }

    public final String toString() {
        int i10;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(super.toString());
        sb2.append(", id:");
        x4.a aVarB = b();
        int iA = aVarB.a(4);
        sb2.append(Integer.toHexString(iA != 0 ? aVarB.f63425b.getInt(iA + aVarB.f63424a) : 0));
        sb2.append(", codepoints:");
        x4.a aVarB2 = b();
        int iA2 = aVarB2.a(16);
        if (iA2 != 0) {
            int i11 = iA2 + aVarB2.f63424a;
            i10 = aVarB2.f63425b.getInt(aVarB2.f63425b.getInt(i11) + i11);
        } else {
            i10 = 0;
        }
        for (int i12 = 0; i12 < i10; i12++) {
            sb2.append(Integer.toHexString(a(i12)));
            sb2.append(" ");
        }
        return sb2.toString();
    }
}
