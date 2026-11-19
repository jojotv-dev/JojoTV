package k4;

import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes3.dex */
public abstract class e0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Map f27756a = Collections.synchronizedMap(new WeakHashMap());

    public static void a(VelocityTracker velocityTracker, MotionEvent motionEvent) {
        velocityTracker.addMovement(motionEvent);
        if (Build.VERSION.SDK_INT < 34 && motionEvent.getSource() == 4194304) {
            Map map = f27756a;
            if (!map.containsKey(velocityTracker)) {
                map.put(velocityTracker, new f0());
            }
            f0 f0Var = (f0) map.get(velocityTracker);
            long[] jArr = f0Var.f27764b;
            long eventTime = motionEvent.getEventTime();
            if (f0Var.f27766d != 0 && eventTime - jArr[f0Var.f27767e] > 40) {
                f0Var.f27766d = 0;
                f0Var.f27765c = 0.0f;
            }
            int i10 = (f0Var.f27767e + 1) % 20;
            f0Var.f27767e = i10;
            int i11 = f0Var.f27766d;
            if (i11 != 20) {
                f0Var.f27766d = i11 + 1;
            }
            f0Var.f27763a[i10] = motionEvent.getAxisValue(26);
            jArr[f0Var.f27767e] = eventTime;
        }
    }

    /* JADX WARN: Code duplicated, block: B:6:0x001e A[PHI: r6
  0x001e: PHI (r6v5 float) = (r6v0 float), (r6v0 float), (r6v0 float), (r6v2 float) binds: [B:5:0x001c, B:12:0x0043, B:16:0x004f, B:18:0x0052] A[DONT_GENERATE, DONT_INLINE]] */
    public static void b(VelocityTracker velocityTracker) {
        long j10;
        float f10;
        float f11;
        float[] fArr;
        float f12 = Float.MAX_VALUE;
        velocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);
        f0 f0Var = (f0) f27756a.get(velocityTracker);
        if (f0Var != null) {
            float[] fArr2 = f0Var.f27763a;
            long[] jArr = f0Var.f27764b;
            int i10 = f0Var.f27766d;
            float fSqrt = 0.0f;
            if (i10 < 2) {
                f10 = Float.MAX_VALUE;
            } else {
                int i11 = f0Var.f27767e;
                int i12 = ((i11 + 20) - (i10 - 1)) % 20;
                long j11 = jArr[i11];
                while (true) {
                    j10 = jArr[i12];
                    if (j11 - j10 <= 100) {
                        break;
                    }
                    f0Var.f27766d--;
                    i12 = (i12 + 1) % 20;
                }
                int i13 = f0Var.f27766d;
                if (i13 < 2) {
                    f10 = Float.MAX_VALUE;
                } else if (i13 == 2) {
                    int i14 = (i12 + 1) % 20;
                    long j12 = jArr[i14];
                    if (j10 != j12) {
                        fSqrt = fArr2[i14] / (j12 - j10);
                    }
                    f10 = Float.MAX_VALUE;
                } else {
                    int i15 = 0;
                    int i16 = 0;
                    float fAbs = 0.0f;
                    while (true) {
                        if (i15 >= f0Var.f27766d - 1) {
                            break;
                        }
                        int i17 = i15 + i12;
                        long j13 = jArr[i17 % 20];
                        int i18 = (i17 + 1) % 20;
                        if (jArr[i18] == j13) {
                            f11 = f12;
                            fArr = fArr2;
                        } else {
                            i16++;
                            f11 = f12;
                            fArr = fArr2;
                            float fSqrt2 = (fAbs < 0.0f ? -1.0f : 1.0f) * ((float) Math.sqrt(Math.abs(fAbs) * 2.0f));
                            float f13 = fArr[i18] / (jArr[i18] - j13);
                            fAbs += Math.abs(f13) * (f13 - fSqrt2);
                            if (i16 == 1) {
                                fAbs *= 0.5f;
                            }
                        }
                        i15++;
                        f12 = f11;
                        fArr2 = fArr;
                    }
                    f10 = f12;
                    fSqrt = (fAbs < 0.0f ? -1.0f : 1.0f) * ((float) Math.sqrt(Math.abs(fAbs) * 2.0f));
                }
            }
            float f14 = fSqrt * 1000;
            f0Var.f27765c = f14;
            if (f14 < (-Math.abs(f10))) {
                f0Var.f27765c = -Math.abs(f10);
            } else if (f0Var.f27765c > Math.abs(f10)) {
                f0Var.f27765c = Math.abs(f10);
            }
        }
    }

    public static float c(VelocityTracker velocityTracker, int i10) {
        if (Build.VERSION.SDK_INT >= 34) {
            return d0.a(velocityTracker, i10);
        }
        if (i10 == 0) {
            return velocityTracker.getXVelocity();
        }
        if (i10 == 1) {
            return velocityTracker.getYVelocity();
        }
        f0 f0Var = (f0) f27756a.get(velocityTracker);
        if (f0Var == null || i10 != 26) {
            return 0.0f;
        }
        return f0Var.f27765c;
    }
}
