package androidx.media3.common;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements q7.m {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2742b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ SimpleBasePlayer.State f2743c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ boolean f2744e;

    public /* synthetic */ d(SimpleBasePlayer.State state, boolean z2, int i10) {
        this.f2742b = i10;
        this.f2743c = state;
        this.f2744e = z2;
    }

    @Override // q7.m
    public final Object get() {
        switch (this.f2742b) {
            case 0:
                return SimpleBasePlayer.lambda$setPlayWhenReady$1(this.f2743c, this.f2744e);
            case 1:
                return SimpleBasePlayer.lambda$setShuffleModeEnabled$9(this.f2743c, this.f2744e);
            case 2:
                return SimpleBasePlayer.lambda$setDeviceMuted$28(this.f2743c, this.f2744e);
            default:
                return SimpleBasePlayer.lambda$setDeviceMuted$29(this.f2743c, this.f2744e);
        }
    }
}
