package com.liskovsoft.youtubeapi.app.potokennp2.generators;

import android.content.Context;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import kotlin.jvm.internal.Ref;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class f implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7679b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Ref.ObjectRef f7680c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Context f7681e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Ref.ObjectRef f7682i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ CountDownLatch f7683m;

    public /* synthetic */ f(Ref.ObjectRef objectRef, Context context, Ref.ObjectRef objectRef2, CountDownLatch countDownLatch, int i10) {
        this.f7679b = i10;
        this.f7680c = objectRef;
        this.f7681e = context;
        this.f7682i = objectRef2;
        this.f7683m = countDownLatch;
    }

    @Override // java.lang.Runnable
    public final void run() throws IOException {
        switch (this.f7679b) {
            case 0:
                PoTokenWebView.Companion.newPoTokenGenerator$lambda$0(this.f7680c, this.f7681e, this.f7682i, this.f7683m);
                break;
            case 1:
                PoTokenWebView2.Companion.newPoTokenGenerator$lambda$0(this.f7680c, this.f7681e, this.f7682i, this.f7683m);
                break;
            case 2:
                PoTokenWebView3.Companion.newPoTokenGenerator$lambda$0(this.f7680c, this.f7681e, this.f7682i, this.f7683m);
                break;
            default:
                PoTokenWebView4.Companion.newPoTokenGenerator$lambda$0(this.f7680c, this.f7681e, this.f7682i, this.f7683m);
                break;
        }
    }
}
