package q2;

import java.util.ArrayList;
import kotlin.collections.CollectionsKt;

/* JADX INFO: loaded from: classes.dex */
public final class w {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f45610a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f45611b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f45612c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f45613d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f45614e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final long f45615f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final long f45616g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final boolean f45617h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int f45618i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final long f45619j;
    public final float k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final long f45620l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final ArrayList f45621m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final long f45622n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public boolean f45623o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public boolean f45624p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public w f45625q;

    public w(long j10, long j11, long j12, boolean z2, float f10, long j13, long j14, boolean z10, boolean z11, int i10, long j15, float f11, long j16) {
        this.f45610a = j10;
        this.f45611b = j11;
        this.f45612c = j12;
        this.f45613d = z2;
        this.f45614e = f10;
        this.f45615f = j13;
        this.f45616g = j14;
        this.f45617h = z10;
        this.f45618i = i10;
        this.f45619j = j15;
        this.k = f11;
        this.f45620l = j16;
        this.f45622n = 0L;
        this.f45623o = z11;
        this.f45624p = z11;
    }

    public final void a() {
        w wVar = this.f45625q;
        if (wVar == null) {
            this.f45623o = true;
            this.f45624p = true;
        } else if (wVar != null) {
            wVar.a();
        }
    }

    public final boolean b() {
        w wVar = this.f45625q;
        if (wVar != null) {
            return wVar.b();
        }
        return this.f45623o || this.f45624p;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("PointerInputChange(id=");
        sb2.append((Object) v.h(this.f45610a));
        sb2.append(", uptimeMillis=");
        sb2.append(this.f45611b);
        sb2.append(", position=");
        sb2.append((Object) c2.b.g(this.f45612c));
        sb2.append(", pressed=");
        sb2.append(this.f45613d);
        sb2.append(", pressure=");
        sb2.append(this.f45614e);
        sb2.append(", previousUptimeMillis=");
        sb2.append(this.f45615f);
        sb2.append(", previousPosition=");
        sb2.append((Object) c2.b.g(this.f45616g));
        sb2.append(", previousPressed=");
        sb2.append(this.f45617h);
        sb2.append(", isConsumed=");
        sb2.append(b());
        sb2.append(", type=");
        sb2.append((Object) h0.a(this.f45618i));
        sb2.append(", historical=");
        Object objEmptyList = this.f45621m;
        if (objEmptyList == null) {
            objEmptyList = CollectionsKt.emptyList();
        }
        sb2.append(objEmptyList);
        sb2.append(", scrollDelta=");
        sb2.append((Object) c2.b.g(this.f45619j));
        sb2.append(", scaleFactor=");
        sb2.append(this.k);
        sb2.append(", panOffset=");
        sb2.append((Object) c2.b.g(this.f45620l));
        sb2.append(')');
        return sb2.toString();
    }

    public w(long j10, long j11, long j12, boolean z2, float f10, long j13, long j14, boolean z10, int i10, ArrayList arrayList, long j15, float f11, long j16, long j17) {
        this(j10, j11, j12, z2, f10, j13, j14, z10, false, i10, j15, f11, j16);
        this.f45621m = arrayList;
        this.f45622n = j17;
    }
}
