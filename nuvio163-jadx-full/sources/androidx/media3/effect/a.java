package androidx.media3.effect;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2831b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ BaseGlShaderProgram f2832c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Exception f2833e;

    public /* synthetic */ a(BaseGlShaderProgram baseGlShaderProgram, Exception exc, int i10) {
        this.f2831b = i10;
        this.f2832c = baseGlShaderProgram;
        this.f2833e = exc;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2831b) {
            case 0:
                this.f2832c.lambda$queueInputFrame$1(this.f2833e);
                break;
            default:
                this.f2832c.lambda$onError$2(this.f2833e);
                break;
        }
    }
}
