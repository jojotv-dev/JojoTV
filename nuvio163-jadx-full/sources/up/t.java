package up;

import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;

/* JADX INFO: loaded from: classes5.dex */
public abstract class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final s f60287a;

    static {
        s sVar = new s("DClass", 2);
        sVar.e("CLASS");
        f60287a = sVar;
        sVar.a(1, HlsMediaPlaylist.Interstitial.SNAP_TYPE_IN);
        sVar.a(3, "CH");
        sVar.b(3);
        Integer numF = b1.f(3);
        sVar.f60067a.put(sVar.d("CHAOS"), numF);
        sVar.a(4, "HS");
        sVar.b(4);
        Integer numF2 = b1.f(4);
        sVar.f60067a.put(sVar.d("HESIOD"), numF2);
        sVar.a(254, "NONE");
        sVar.a(255, "ANY");
    }

    public static void a(int i10) {
        if (i10 < 0 || i10 > 65535) {
            StringBuffer stringBuffer = new StringBuffer("Invalid DNS class: ");
            stringBuffer.append(i10);
            throw new n0(stringBuffer.toString());
        }
    }
}
