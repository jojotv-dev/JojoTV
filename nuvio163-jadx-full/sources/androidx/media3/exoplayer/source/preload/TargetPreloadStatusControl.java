package androidx.media3.exoplayer.source.preload;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface TargetPreloadStatusControl<T, PreloadStatusT> {
    PreloadStatusT getTargetPreloadStatus(T t10);
}
