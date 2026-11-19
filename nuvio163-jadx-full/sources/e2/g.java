package e2;

import d2.a0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final c f13763a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c f13764b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final c f13765c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float[] f13766d;

    public g(c cVar, c cVar2, c cVar3, float[] fArr) {
        this.f13763a = cVar;
        this.f13764b = cVar2;
        this.f13765c = cVar3;
        this.f13766d = fArr;
    }

    public long a(long j10) {
        float fH = a0.h(j10);
        float fG = a0.g(j10);
        float fE = a0.e(j10);
        float fD = a0.d(j10);
        c cVar = this.f13764b;
        long jD = cVar.d(fH, fG, fE);
        float fIntBitsToFloat = Float.intBitsToFloat((int) (jD >> 32));
        float fIntBitsToFloat2 = Float.intBitsToFloat((int) (jD & 4294967295L));
        float fE2 = cVar.e(fH, fG, fE);
        float[] fArr = this.f13766d;
        if (fArr != null) {
            fIntBitsToFloat *= fArr[0];
            fIntBitsToFloat2 *= fArr[1];
            fE2 *= fArr[2];
        }
        float f10 = fIntBitsToFloat;
        float f11 = fIntBitsToFloat2;
        return this.f13765c.f(f10, f11, fE2, fD, this.f13763a);
    }

    /* JADX WARN: Code duplicated, block: B:28:0x006e  */
    /* JADX WARN: Illegal instructions before constructor call */
    public g(c cVar, c cVar2, int i10) {
        float[] fArr;
        long j10 = cVar.f13734b;
        long j11 = b.f13728a;
        c cVarA = b.a(j10, j11) ? j.a(cVar) : cVar;
        c cVarA2 = b.a(cVar2.f13734b, j11) ? j.a(cVar2) : cVar2;
        if (i10 == 3) {
            boolean zA = b.a(cVar.f13734b, j11);
            boolean zA2 = b.a(cVar2.f13734b, j11);
            if (!(zA && zA2) && (zA || zA2)) {
                cVar = zA ? cVar : cVar2;
                Intrinsics.checkNotNull(cVar, "null cannot be cast to non-null type androidx.compose.ui.graphics.colorspace.Rgb");
                s sVar = ((q) cVar).f13787d;
                float[] fArrA = j.f13772e;
                float[] fArrA2 = zA ? sVar.a() : fArrA;
                fArrA = zA2 ? sVar.a() : fArrA;
                fArr = new float[]{fArrA2[0] / fArrA[0], fArrA2[1] / fArrA[1], fArrA2[2] / fArrA[2]};
            } else {
                fArr = null;
            }
        } else {
            fArr = null;
        }
        this(cVar2, cVarA, cVarA2, fArr);
    }
}
