package o8;

import androidx.media3.extractor.ts.PsExtractor;

/* JADX INFO: loaded from: classes4.dex */
public enum g {
    UNIVERSAL(0),
    APPLICATION(64),
    CONTEXT_SPECIFIC(128),
    /* JADX INFO: Fake field, exist only in values array */
    PRIVATE(PsExtractor.AUDIO_STREAM);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f43707b;

    g(int i10) {
        this.f43707b = i10;
    }
}
