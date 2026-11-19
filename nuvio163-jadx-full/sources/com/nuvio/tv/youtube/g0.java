package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import okhttp3.internal.ws.RealWebSocket;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class g0 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10434b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ long f10435c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f10436e;

    public /* synthetic */ g0(Object obj, long j10, int i10) {
        this.f10434b = i10;
        this.f10436e = obj;
        this.f10435c = j10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10434b) {
            case 0:
                return YouTubePlayerPanelKt.RecommendationsContent$lambda$0$1$0$0$0$0$0$0$0((Function1) this.f10436e, this.f10435c);
            default:
                return Long.valueOf(RealWebSocket.initReaderAndWriter$lambda$0$0((RealWebSocket) this.f10436e, this.f10435c));
        }
    }
}
