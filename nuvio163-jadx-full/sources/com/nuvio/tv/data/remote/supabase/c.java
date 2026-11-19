package com.nuvio.tv.data.remote.supabase;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class c implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10255b;

    public /* synthetic */ c(int i10) {
        this.f10255b = i10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10255b) {
            case 0:
                return LibraryMutationDto._childSerializers$_anonymous_();
            case 1:
                return LibrarySnapshotDto._childSerializers$_anonymous_();
            case 2:
                return MemberAccessResponse._childSerializers$_anonymous_();
            case 3:
                return StoredAvatarCatalogPayload._childSerializers$_anonymous_();
            case 4:
                return StoredAvatarCatalogPayload._childSerializers$_anonymous_$0();
            case 5:
                return StoredProfileBackgroundCatalogPayload._childSerializers$_anonymous_();
            default:
                return SupabaseMediaServers._childSerializers$_anonymous_();
        }
    }
}
