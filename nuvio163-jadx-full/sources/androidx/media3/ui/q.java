package androidx.media3.ui;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class q implements q7.i {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3786b;

    public /* synthetic */ q(int i10) {
        this.f3786b = i10;
    }

    @Override // q7.i
    public final boolean apply(Object obj) {
        switch (this.f3786b) {
            case 0:
                return SubtitleViewUtils.lambda$removeAllEmbeddedStyling$0(obj);
            default:
                return SubtitleViewUtils.lambda$removeEmbeddedFontSizes$1(obj);
        }
    }
}
