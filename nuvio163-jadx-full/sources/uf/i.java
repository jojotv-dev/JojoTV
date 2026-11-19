package uf;

/* JADX INFO: loaded from: classes4.dex */
public final class i implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.j f59784b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ float f59785c;

    public i(a0.e eVar, float f10) {
        this.f59785c = f10;
        this.f59784b = eVar.getScrollAnimationSpec();
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        return f10 - this.f59785c;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f59784b;
    }
}
