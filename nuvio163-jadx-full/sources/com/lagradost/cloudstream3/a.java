package com.lagradost.cloudstream3;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7590b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ LoadResponse f7591c;

    public /* synthetic */ a(LoadResponse loadResponse, int i10) {
        this.f7590b = i10;
        this.f7591c = loadResponse;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f7590b) {
            case 0:
                return LoadResponse.Companion.getTMDbId$lambda$0(this.f7591c);
            default:
                return LoadResponse.Companion.getImdbId$lambda$0(this.f7591c);
        }
    }
}
