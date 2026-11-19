package androidx.media3.common;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class g implements q7.m {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2750b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ SimpleBasePlayer.State f2751c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f2752e;

    public /* synthetic */ g(SimpleBasePlayer.State state, int i10, int i11) {
        this.f2750b = i11;
        this.f2751c = state;
        this.f2752e = i10;
    }

    @Override // q7.m
    public final Object get() {
        switch (this.f2750b) {
            case 0:
                return SimpleBasePlayer.lambda$setDeviceVolume$22(this.f2751c, this.f2752e);
            case 1:
                return SimpleBasePlayer.lambda$setDeviceVolume$23(this.f2751c, this.f2752e);
            default:
                return SimpleBasePlayer.lambda$setRepeatMode$8(this.f2751c, this.f2752e);
        }
    }
}
