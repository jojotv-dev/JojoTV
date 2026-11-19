package com.nuvio.vpn.android;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class h0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10885b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ p0 f10886c;

    public /* synthetic */ h0(p0 p0Var, int i10) {
        this.f10885b = i10;
        this.f10886c = p0Var;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f10885b) {
            case 0:
                r0.c(this.f10886c);
                break;
            case 1:
                r0.n(this.f10886c);
                break;
            default:
                r0.c(this.f10886c);
                break;
        }
    }
}
