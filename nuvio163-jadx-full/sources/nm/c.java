package nm;

import java.io.Serializable;

/* JADX INFO: loaded from: classes5.dex */
public final class c implements Serializable, Cloneable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public s f42951b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f42952c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f42953e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f42954i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public boolean f42955m = false;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f42956n = false;

    public c(int i10) {
        this.f42952c = i10;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final c clone() {
        c cVar = new c(this.f42952c);
        cVar.f42951b = this.f42951b;
        cVar.f42953e = this.f42953e;
        cVar.f42954i = this.f42954i;
        cVar.f42955m = this.f42955m;
        cVar.f42956n = this.f42956n;
        return cVar;
    }

    public final int b() {
        if (!this.f42955m || this.f42956n) {
            return Integer.MAX_VALUE;
        }
        return this.f42953e;
    }
}
