package com.liskovsoft.youtubeapi.app.potokennp2;

import com.liskovsoft.youtubeapi.app.potokennp2.core.PoTokenGenerator;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7663b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PoTokenGenerator f7664c;

    public /* synthetic */ a(PoTokenGenerator poTokenGenerator, int i10) {
        this.f7663b = i10;
        this.f7664c = poTokenGenerator;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f7663b) {
            case 0:
                return TvPoTokenProvider.release$lambda$0$0$0(this.f7664c);
            default:
                return WebPoTokenProvider.release$lambda$0$0$0(this.f7664c);
        }
    }
}
