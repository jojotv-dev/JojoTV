package com.liskovsoft.youtubeapi.app.potokennp2.generators;

import java.util.concurrent.CountDownLatch;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class c implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7670b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ CountDownLatch f7671c;

    public /* synthetic */ c(CountDownLatch countDownLatch, int i10) {
        this.f7670b = i10;
        this.f7671c = countDownLatch;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f7670b) {
            case 0:
                return PoTokenV8.Companion.newPoTokenGenerator$lambda$0(this.f7671c);
            case 1:
                return PoTokenWebView.Companion.newPoTokenGenerator$lambda$0$0(this.f7671c);
            case 2:
                return PoTokenWebView2.Companion.newPoTokenGenerator$lambda$0$0(this.f7671c);
            case 3:
                return PoTokenWebView3.Companion.newPoTokenGenerator$lambda$0$0(this.f7671c);
            case 4:
                return PoTokenWebView4.Companion.newPoTokenGenerator$lambda$0$0(this.f7671c);
            default:
                return TvPoTokenWebView4.Companion.newPoTokenGenerator$lambda$0$0(this.f7671c);
        }
    }
}
