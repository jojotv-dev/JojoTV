package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements Function2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10331b;

    public /* synthetic */ b(int i10) {
        this.f10331b = i10;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        switch (this.f10331b) {
            case 0:
                return ComposableSingletons$YouTubeScreenKt.lambda_726217430$lambda$0((g1.o) obj, ((Integer) obj2).intValue());
            default:
                return YouTubeScreenKt.Content$lambda$0$0(((Integer) obj).intValue(), ((Integer) obj2).intValue());
        }
    }
}
