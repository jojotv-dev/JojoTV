package d2;

import android.graphics.Path;
import android.graphics.RectF;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public interface u0 {
    static void a(u0 u0Var, c2.d dVar) {
        t0[] t0VarArr = t0.f11766b;
        l lVar = (l) u0Var;
        if (lVar.f11743b == null) {
            lVar.f11743b = new RectF();
        }
        RectF rectF = lVar.f11743b;
        Intrinsics.checkNotNull(rectF);
        float f10 = dVar.f6416a;
        long j10 = dVar.f6423h;
        long j11 = dVar.f6422g;
        long j12 = dVar.f6421f;
        long j13 = dVar.f6420e;
        rectF.set(f10, dVar.f6417b, dVar.f6418c, dVar.f6419d);
        if (lVar.f11744c == null) {
            lVar.f11744c = new float[8];
        }
        float[] fArr = lVar.f11744c;
        Intrinsics.checkNotNull(fArr);
        fArr[0] = Float.intBitsToFloat((int) (j13 >> 32));
        fArr[1] = Float.intBitsToFloat((int) (j13 & 4294967295L));
        fArr[2] = Float.intBitsToFloat((int) (j12 >> 32));
        fArr[3] = Float.intBitsToFloat((int) (j12 & 4294967295L));
        fArr[4] = Float.intBitsToFloat((int) (j11 >> 32));
        fArr[5] = Float.intBitsToFloat((int) (j11 & 4294967295L));
        fArr[6] = Float.intBitsToFloat((int) (j10 >> 32));
        fArr[7] = Float.intBitsToFloat((int) (j10 & 4294967295L));
        Path path = lVar.f11742a;
        RectF rectF2 = lVar.f11743b;
        Intrinsics.checkNotNull(rectF2);
        float[] fArr2 = lVar.f11744c;
        Intrinsics.checkNotNull(fArr2);
        path.addRoundRect(rectF2, fArr2, Path.Direction.CCW);
    }

    static void b(l lVar, l lVar2) {
        lVar.f11742a.addPath(lVar2.f11742a, Float.intBitsToFloat((int) 0), Float.intBitsToFloat((int) 0));
    }
}
