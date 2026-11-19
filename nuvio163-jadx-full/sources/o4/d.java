package o4;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import androidx.recyclerview.widget.k;
import o.s1;

/* JADX INFO: loaded from: classes3.dex */
public final class d implements View.OnTouchListener {

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final int f43464z = ViewConfiguration.getTapTimeout();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final a f43465b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final AccelerateInterpolator f43466c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final s1 f43467e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public k f43468i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final float[] f43469m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final float[] f43470n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final int f43471o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final int f43472p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final float[] f43473q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final float[] f43474r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public final float[] f43475s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public boolean f43476t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public boolean f43477u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public boolean f43478v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public boolean f43479w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public boolean f43480x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public final s1 f43481y;

    public d(s1 s1Var) {
        a aVar = new a();
        aVar.f43459e = Long.MIN_VALUE;
        aVar.f43461g = -1L;
        aVar.f43460f = 0L;
        this.f43465b = aVar;
        this.f43466c = new AccelerateInterpolator();
        float[] fArr = {0.0f, 0.0f};
        this.f43469m = fArr;
        float[] fArr2 = {Float.MAX_VALUE, Float.MAX_VALUE};
        this.f43470n = fArr2;
        float[] fArr3 = {0.0f, 0.0f};
        this.f43473q = fArr3;
        float[] fArr4 = {0.0f, 0.0f};
        this.f43474r = fArr4;
        float[] fArr5 = {Float.MAX_VALUE, Float.MAX_VALUE};
        this.f43475s = fArr5;
        this.f43467e = s1Var;
        float f10 = Resources.getSystem().getDisplayMetrics().density;
        float f11 = ((int) ((1575.0f * f10) + 0.5f)) / 1000.0f;
        fArr5[0] = f11;
        fArr5[1] = f11;
        float f12 = ((int) ((f10 * 315.0f) + 0.5f)) / 1000.0f;
        fArr4[0] = f12;
        fArr4[1] = f12;
        this.f43471o = 1;
        fArr2[0] = Float.MAX_VALUE;
        fArr2[1] = Float.MAX_VALUE;
        fArr[0] = 0.2f;
        fArr[1] = 0.2f;
        fArr3[0] = 0.001f;
        fArr3[1] = 0.001f;
        this.f43472p = f43464z;
        aVar.f43455a = 500;
        aVar.f43456b = 500;
        this.f43481y = s1Var;
    }

    public static float b(float f10, float f11, float f12) {
        if (f10 > f12) {
            return f12;
        }
        return f10 < f11 ? f11 : f10;
    }

    /* JADX WARN: Code duplicated, block: B:12:0x003b A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:13:0x003c  */
    /* JADX WARN: Code duplicated, block: B:15:0x004b  */
    /* JADX WARN: Code duplicated, block: B:17:0x0051  */
    public final float a(int i10, float f10, float f11, float f12) {
        float fB;
        float interpolation;
        float fB2 = b(this.f43469m[i10] * f11, 0.0f, this.f43470n[i10]);
        float fC = c(f11 - f10, fB2) - c(f10, fB2);
        AccelerateInterpolator accelerateInterpolator = this.f43466c;
        if (fC >= 0.0f) {
            if (fC > 0.0f) {
                interpolation = accelerateInterpolator.getInterpolation(fC);
            } else {
                fB = 0.0f;
            }
            if (fB == 0.0f) {
                return 0.0f;
            }
            float f13 = this.f43473q[i10];
            float f14 = this.f43474r[i10];
            float f15 = this.f43475s[i10];
            float f16 = f13 * f12;
            return fB > 0.0f ? b(fB * f16, f14, f15) : -b((-fB) * f16, f14, f15);
        }
        interpolation = -accelerateInterpolator.getInterpolation(-fC);
        fB = b(interpolation, -1.0f, 1.0f);
        if (fB == 0.0f) {
            return 0.0f;
        }
        float f17 = this.f43473q[i10];
        float f18 = this.f43474r[i10];
        float f19 = this.f43475s[i10];
        float f110 = f17 * f12;
        if (fB > 0.0f) {
        }
    }

    public final float c(float f10, float f11) {
        if (f11 != 0.0f) {
            int i10 = this.f43471o;
            if (i10 == 0 || i10 == 1) {
                if (f10 < f11) {
                    if (f10 >= 0.0f) {
                        return 1.0f - (f10 / f11);
                    }
                    if (this.f43479w && i10 == 1) {
                        return 1.0f;
                    }
                }
            } else if (i10 == 2 && f10 < 0.0f) {
                return f10 / (-f11);
            }
        }
        return 0.0f;
    }

    public final void d() {
        int i10 = 0;
        if (this.f43477u) {
            this.f43479w = false;
            return;
        }
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        a aVar = this.f43465b;
        int i11 = (int) (jCurrentAnimationTimeMillis - aVar.f43459e);
        int i12 = aVar.f43456b;
        if (i11 > i12) {
            i10 = i12;
        } else if (i11 >= 0) {
            i10 = i11;
        }
        aVar.f43463i = i10;
        aVar.f43462h = aVar.a(jCurrentAnimationTimeMillis);
        aVar.f43461g = jCurrentAnimationTimeMillis;
    }

    public final boolean e() {
        s1 s1Var;
        int count;
        a aVar = this.f43465b;
        float f10 = aVar.f43458d;
        int iAbs = (int) (f10 / Math.abs(f10));
        Math.abs(aVar.f43457c);
        if (iAbs != 0 && (count = (s1Var = this.f43481y).getCount()) != 0) {
            int childCount = s1Var.getChildCount();
            int firstVisiblePosition = s1Var.getFirstVisiblePosition();
            int i10 = firstVisiblePosition + childCount;
            if (iAbs <= 0 ? !(iAbs >= 0 || (firstVisiblePosition <= 0 && s1Var.getChildAt(0).getTop() >= 0)) : !(i10 >= count && s1Var.getChildAt(childCount - 1).getBottom() <= s1Var.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0014, code lost:
    
        if (r0 != 3) goto L30;
     */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
        /*
            r7 = this;
            boolean r0 = r7.f43480x
            r1 = 0
            if (r0 != 0) goto L7
            goto L7e
        L7:
            int r0 = r9.getActionMasked()
            r2 = 1
            if (r0 == 0) goto L1b
            if (r0 == r2) goto L17
            r3 = 2
            if (r0 == r3) goto L1f
            r8 = 3
            if (r0 == r8) goto L17
            goto L7e
        L17:
            r7.d()
            return r1
        L1b:
            r7.f43478v = r2
            r7.f43476t = r1
        L1f:
            float r0 = r9.getX()
            int r3 = r8.getWidth()
            float r3 = (float) r3
            o.s1 r4 = r7.f43467e
            int r5 = r4.getWidth()
            float r5 = (float) r5
            float r0 = r7.a(r1, r0, r3, r5)
            float r9 = r9.getY()
            int r8 = r8.getHeight()
            float r8 = (float) r8
            int r3 = r4.getHeight()
            float r3 = (float) r3
            float r8 = r7.a(r2, r9, r8, r3)
            o4.a r9 = r7.f43465b
            r9.f43457c = r0
            r9.f43458d = r8
            boolean r8 = r7.f43479w
            if (r8 != 0) goto L7e
            boolean r8 = r7.e()
            if (r8 == 0) goto L7e
            androidx.recyclerview.widget.k r8 = r7.f43468i
            if (r8 != 0) goto L62
            androidx.recyclerview.widget.k r8 = new androidx.recyclerview.widget.k
            r9 = 14
            r8.<init>(r7, r9)
            r7.f43468i = r8
        L62:
            r7.f43479w = r2
            r7.f43477u = r2
            boolean r8 = r7.f43476t
            if (r8 != 0) goto L77
            int r8 = r7.f43472p
            if (r8 <= 0) goto L77
            androidx.recyclerview.widget.k r9 = r7.f43468i
            long r5 = (long) r8
            java.util.WeakHashMap r8 = k4.t0.f27843a
            r4.postOnAnimationDelayed(r9, r5)
            goto L7c
        L77:
            androidx.recyclerview.widget.k r8 = r7.f43468i
            r8.run()
        L7c:
            r7.f43476t = r2
        L7e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: o4.d.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}
