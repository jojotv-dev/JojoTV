package n7;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public final class x1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final s0 f39770a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f39771b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f39772c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Paint f39773d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Paint f39774e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public c2.a f39775f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public c2.a f39776g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public boolean f39777h;

    public x1() {
        Paint paint = new Paint();
        this.f39773d = paint;
        paint.setFlags(193);
        paint.setHinting(0);
        paint.setStyle(Paint.Style.FILL);
        Typeface typeface = Typeface.DEFAULT;
        paint.setTypeface(typeface);
        Paint paint2 = new Paint();
        this.f39774e = paint2;
        paint2.setFlags(193);
        paint2.setHinting(0);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setTypeface(typeface);
        this.f39770a = s0.a();
    }

    public x1(x1 x1Var) {
        this.f39771b = x1Var.f39771b;
        this.f39772c = x1Var.f39772c;
        this.f39773d = new Paint(x1Var.f39773d);
        this.f39774e = new Paint(x1Var.f39774e);
        c2.a aVar = x1Var.f39775f;
        if (aVar != null) {
            this.f39775f = new c2.a(aVar);
        }
        c2.a aVar2 = x1Var.f39776g;
        if (aVar2 != null) {
            this.f39776g = new c2.a(aVar2);
        }
        this.f39777h = x1Var.f39777h;
        try {
            this.f39770a = (s0) x1Var.f39770a.clone();
        } catch (CloneNotSupportedException e10) {
            Log.e("SVGAndroidRenderer", "Unexpected clone error", e10);
            this.f39770a = s0.a();
        }
    }
}
