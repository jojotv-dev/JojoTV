package o7;

import java.util.Stack;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public final class w0 extends z0 {

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f43674m;

    @Override // io.sentry.internal.debugmeta.c
    public final boolean N(m1 m1Var) {
        m1Var.f43614d = false;
        return ((io.sentry.internal.debugmeta.c) this.f25625c).N(m1Var);
    }

    @Override // o7.z0, io.sentry.internal.debugmeta.c
    public final boolean y(b bVar, int i10, CharSequence charSequence) {
        Vector vector = bVar.f43540h;
        int i11 = this.f43683i;
        Integer num = (Integer) ((Stack) vector.get(i11)).pop();
        int iIntValue = num.intValue();
        int[] iArr = bVar.k;
        int i12 = this.f43674m;
        int i13 = i12 * 2;
        int i14 = iArr[i13];
        int i15 = i13 + 1;
        int i16 = iArr[i15];
        if (i12 > 0) {
            iArr[i13] = iIntValue;
            iArr[i15] = i10;
        }
        boolean zY = T(bVar).y(bVar, i10, charSequence);
        if (i12 > 0 && !zY) {
            int[] iArr2 = bVar.k;
            iArr2[i13] = i14;
            iArr2[i15] = i16;
        }
        ((Stack) bVar.f43540h.get(i11)).push(num);
        return zY;
    }
}
