package com.liskovsoft.youtubeapi.app.potokennp2.generators;

import java.util.concurrent.CountDownLatch;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Ref;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class d implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7672b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Ref.ObjectRef f7673c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ CountDownLatch f7674e;

    public /* synthetic */ d(Ref.ObjectRef objectRef, CountDownLatch countDownLatch, int i10) {
        this.f7672b = i10;
        this.f7673c = objectRef;
        this.f7674e = countDownLatch;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f7672b) {
            case 0:
                return PoTokenWebView.generatePoToken$lambda$0(this.f7673c, this.f7674e, (String) obj);
            case 1:
                return PoTokenWebView2.generatePoToken$lambda$0(this.f7673c, this.f7674e, (String) obj);
            case 2:
                return PoTokenWebView3.generatePoToken$lambda$0(this.f7673c, this.f7674e, (String) obj);
            case 3:
                return PoTokenWebView4.generatePoToken$lambda$0(this.f7673c, this.f7674e, (String) obj);
            default:
                return TvPoTokenWebView4.generatePoToken$lambda$0(this.f7673c, this.f7674e, (String) obj);
        }
    }
}
