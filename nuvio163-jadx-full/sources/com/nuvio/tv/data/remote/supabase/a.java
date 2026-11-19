package com.nuvio.tv.data.remote.supabase;

import kotlin.jvm.functions.Function1;
import oi.j;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10254b;

    public /* synthetic */ a(int i10) {
        this.f10254b = i10;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        j jVar = (j) obj;
        switch (this.f10254b) {
            case 0:
                return AvatarRepository.json$lambda$0(jVar);
            default:
                return ProfileBackgroundRepository.json$lambda$0(jVar);
        }
    }
}
