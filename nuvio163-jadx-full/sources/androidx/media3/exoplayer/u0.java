package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class u0 implements q7.f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3351a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f3352b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f3353c;

    public /* synthetic */ u0(Object obj, int i10, int i11) {
        this.f3351a = i11;
        this.f3352b = obj;
        this.f3353c = i10;
    }

    @Override // q7.f
    public final Object apply(Object obj) {
        switch (this.f3351a) {
            case 0:
                return ((StreamVolumeManager) this.f3352b).lambda$increaseVolume$6(this.f3353c, (StreamVolumeManager.StreamVolumeState) obj);
            case 1:
                return ((StreamVolumeManager) this.f3352b).lambda$decreaseVolume$8(this.f3353c, (StreamVolumeManager.StreamVolumeState) obj);
            case 2:
                return ((StreamVolumeManager) this.f3352b).lambda$setStreamType$2(this.f3353c, (StreamVolumeManager.StreamVolumeState) obj);
            default:
                return ((ExoPlayerImpl) this.f3352b).lambda$setAudioSessionId$11(this.f3353c, (Integer) obj);
        }
    }
}
