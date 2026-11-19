package la;

import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;

/* JADX INFO: loaded from: classes4.dex */
public enum h {
    GTE(">="),
    LTE("<="),
    EQ("=="),
    TSEQ("==="),
    NE("!="),
    TSNE("!=="),
    LT("<"),
    GT(">"),
    REGEX("=~"),
    NIN("NIN"),
    IN(HlsMediaPlaylist.Interstitial.SNAP_TYPE_IN),
    CONTAINS("CONTAINS"),
    ALL("ALL"),
    SIZE("SIZE"),
    EXISTS("EXISTS"),
    TYPE("TYPE"),
    MATCHES("MATCHES"),
    EMPTY("EMPTY"),
    SUBSETOF("SUBSETOF");


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f32219b;

    h(String str) {
        this.f32219b = str;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f32219b;
    }
}
