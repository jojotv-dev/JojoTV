package androidx.media3.datasource;

import androidx.media3.exoplayer.analytics.DefaultPlaybackSessionManager;
import q7.m;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class b implements m {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2818b;

    public /* synthetic */ b(int i10) {
        this.f2818b = i10;
    }

    @Override // q7.m
    public final Object get() {
        switch (this.f2818b) {
            case 0:
                return DataSourceBitmapLoader.lambda$static$0();
            case 1:
                return DefaultPlaybackSessionManager.generateDefaultSessionId();
            default:
                throw new IllegalStateException();
        }
    }
}
