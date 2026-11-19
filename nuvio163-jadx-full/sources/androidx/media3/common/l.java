package androidx.media3.common;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class l implements q7.m {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2769b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ SimpleBasePlayer.State f2770c;

    public /* synthetic */ l(SimpleBasePlayer.State state, int i10) {
        this.f2769b = i10;
        this.f2770c = state;
    }

    @Override // q7.m
    public final Object get() {
        switch (this.f2769b) {
            case 0:
                return SimpleBasePlayer.lambda$setVideoSurface$17(this.f2770c);
            case 1:
                return SimpleBasePlayer.lambda$increaseDeviceVolume$25(this.f2770c);
            case 2:
                return SimpleBasePlayer.lambda$decreaseDeviceVolume$26(this.f2770c);
            case 3:
                return SimpleBasePlayer.lambda$release$13(this.f2770c);
            case 4:
                return SimpleBasePlayer.lambda$clearVideoOutput$21(this.f2770c);
            case 5:
                return SimpleBasePlayer.lambda$increaseDeviceVolume$24(this.f2770c);
            case 6:
                return SimpleBasePlayer.lambda$prepare$7(this.f2770c);
            default:
                return SimpleBasePlayer.lambda$decreaseDeviceVolume$27(this.f2770c);
        }
    }
}
