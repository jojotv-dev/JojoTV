package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class z implements q7.f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3409a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3410b;

    public /* synthetic */ z(int i10, int i11) {
        this.f3409a = i11;
        this.f3410b = i10;
    }

    @Override // q7.f
    public final Object apply(Object obj) {
        switch (this.f3409a) {
            case 0:
                return ExoPlayerImpl.lambda$setAudioSessionId$10(this.f3410b, (Integer) obj);
            case 1:
                return ExoPlayerImpl.ComponentListener.lambda$onAudioSessionIdChanged$2(this.f3410b, (Integer) obj);
            case 2:
                return ExoPlayerImpl.ComponentListener.lambda$onAudioSessionIdChanged$3(this.f3410b, (Integer) obj);
            case 3:
                return StreamVolumeManager.lambda$setVolume$3(this.f3410b, (StreamVolumeManager.StreamVolumeState) obj);
            default:
                return StreamVolumeManager.lambda$setStreamType$1(this.f3410b, (StreamVolumeManager.StreamVolumeState) obj);
        }
    }
}
