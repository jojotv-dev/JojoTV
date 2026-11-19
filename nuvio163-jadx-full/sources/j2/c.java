package j2;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f27024a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f27025b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f27026c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f27027d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f27028e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final float f27029f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final float f27030g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final float f27031h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final List f27032i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final ArrayList f27033j;

    public c(String str, float f10, float f11, float f12, float f13, float f14, float f15, float f16, List list, int i10) {
        str = (i10 & 1) != 0 ? "" : str;
        f10 = (i10 & 2) != 0 ? 0.0f : f10;
        f11 = (i10 & 4) != 0 ? 0.0f : f11;
        f12 = (i10 & 8) != 0 ? 0.0f : f12;
        f13 = (i10 & 16) != 0 ? 1.0f : f13;
        f14 = (i10 & 32) != 0 ? 1.0f : f14;
        f15 = (i10 & 64) != 0 ? 0.0f : f15;
        f16 = (i10 & 128) != 0 ? 0.0f : f16;
        list = (i10 & 256) != 0 ? h0.f27109a : list;
        ArrayList arrayList = new ArrayList();
        this.f27024a = str;
        this.f27025b = f10;
        this.f27026c = f11;
        this.f27027d = f12;
        this.f27028e = f13;
        this.f27029f = f14;
        this.f27030g = f15;
        this.f27031h = f16;
        this.f27032i = list;
        this.f27033j = arrayList;
    }
}
