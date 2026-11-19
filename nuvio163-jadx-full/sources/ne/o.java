package ne;

import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final o f41128b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final o f41129c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final o f41130e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final o f41131i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ o[] f41132m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f41133n;

    static {
        o oVar = new o(HlsMediaPlaylist.Interstitial.CUE_TRIGGER_PRE, 0);
        f41128b = oVar;
        o oVar2 = new o("LIVE", 1);
        f41129c = oVar2;
        o oVar3 = new o(HlsMediaPlaylist.Interstitial.CUE_TRIGGER_POST, 2);
        f41130e = oVar3;
        o oVar4 = new o("OFF", 3);
        f41131i = oVar4;
        o[] oVarArr = {oVar, oVar2, oVar3, oVar4};
        f41132m = oVarArr;
        f41133n = EnumEntriesKt.enumEntries(oVarArr);
    }

    public static o valueOf(String str) {
        return (o) Enum.valueOf(o.class, str);
    }

    public static o[] values() {
        return (o[]) f41132m.clone();
    }
}
