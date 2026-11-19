package f1;

import androidx.media3.extractor.metadata.dvbsi.AppInfoTableDecoder;
import androidx.media3.extractor.ts.PsExtractor;
import androidx.media3.extractor.ts.TsExtractor;
import com.liskovsoft.sharedutils.helpers.PermissionHelpers;
import d2.j0;
import jcifs.SmbConstants;
import jcifs.netbios.NbtException;
import jcifs.smb.WinError;
import okhttp3.internal.http.HttpStatusCodesKt;
import okhttp3.internal.ws.WebSocketProtocol;

/* JADX INFO: loaded from: classes3.dex */
public abstract class p {
    public static final long A;
    public static final long B;
    public static final long C;
    public static final long D;
    public static final long E;
    public static final long F;
    public static final long G;
    public static final long H;
    public static final long I;

    /* JADX INFO: renamed from: J, reason: collision with root package name */
    public static final long f15964J;
    public static final long K;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final long f15965a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f15966b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final long f15967c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final long f15968d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final long f15969e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final long f15970f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final long f15971g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final long f15972h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final long f15973i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final long f15974j;
    public static final long k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final long f15975l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final long f15976m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final long f15977n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final long f15978o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final long f15979p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final long f15980q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final long f15981r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final long f15982s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final long f15983t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final long f15984u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public static final long f15985v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public static final long f15986w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public static final long f15987x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final long f15988y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final long f15989z;

    static {
        j0.d(0, 0, 0, 255);
        j0.d(0, 0, 0, 255);
        f15965a = j0.d(65, 14, 11, 255);
        f15966b = j0.d(255, 255, 255, 255);
        j0.d(96, 20, 16, 255);
        j0.d(140, 29, 24, 255);
        f15967c = j0.d(179, 38, 30, 255);
        j0.d(220, 54, 46, 255);
        j0.d(228, 105, 98, 255);
        j0.d(236, 146, 142, 255);
        j0.d(242, 184, 181, 255);
        f15968d = j0.d(249, 222, 220, 255);
        j0.d(252, 238, 238, 255);
        j0.d(255, 251, 249, 255);
        f15969e = j0.d(0, 0, 0, 255);
        f15970f = j0.d(29, 27, 32, 255);
        f15971g = j0.d(255, 255, 255, 255);
        j0.d(33, 31, 38, 255);
        j0.d(43, 41, 48, 255);
        f15972h = j0.d(50, 47, 53, 255);
        j0.d(54, 52, 59, 255);
        j0.d(59, 56, 62, 255);
        j0.d(72, 70, 76, 255);
        j0.d(15, 13, 19, 255);
        j0.d(96, 93, 100, 255);
        j0.d(121, 118, 125, 255);
        j0.d(20, 18, 24, 255);
        j0.d(147, NbtException.UNSPECIFIED, 150, 255);
        j0.d(174, 169, 177, 255);
        j0.d(202, 197, 205, 255);
        f15973i = j0.d(222, 216, 225, 255);
        f15974j = j0.d(WinError.ERROR_BAD_PIPE, 224, WinError.ERROR_PIPE_NOT_CONNECTED, 255);
        k = j0.d(236, WinError.ERROR_BAD_PIPE, PsExtractor.VIDEO_STREAM_MASK, 255);
        f15975l = j0.d(243, 237, 247, 255);
        f15976m = j0.d(245, 239, 247, 255);
        f15977n = j0.d(247, 242, SmbConstants.DEFAULT_SSN_LIMIT, 255);
        f15978o = j0.d(254, 247, 255, 255);
        j0.d(255, 251, 255, 255);
        j0.d(0, 0, 0, 255);
        j0.d(29, 26, 34, 255);
        j0.d(255, 255, 255, 255);
        j0.d(50, 47, 55, 255);
        f15979p = j0.d(73, 69, 79, 255);
        j0.d(96, 93, HttpStatusCodesKt.HTTP_PROCESSING, 255);
        f15980q = j0.d(121, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, WebSocketProtocol.PAYLOAD_SHORT, 255);
        j0.d(147, NbtException.UNSPECIFIED, 153, 255);
        j0.d(174, 169, 180, 255);
        f15981r = j0.d(202, 196, 208, 255);
        f15982s = j0.d(WinError.ERROR_PIPE_BUSY, 224, 236, 255);
        j0.d(245, 238, SmbConstants.DEFAULT_SSN_LIMIT, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        f15983t = j0.d(33, 0, 93, 255);
        f15984u = j0.d(255, 255, 255, 255);
        j0.d(56, 30, PermissionHelpers.REQUEST_OVERLAY, 255);
        f15985v = j0.d(79, 55, TsExtractor.TS_STREAM_TYPE_DTS_UHD, 255);
        f15986w = j0.d(HttpStatusCodesKt.HTTP_EARLY_HINTS, 80, 164, 255);
        j0.d(127, HttpStatusCodesKt.HTTP_EARLY_HINTS, 190, 255);
        j0.d(154, 130, 219, 255);
        j0.d(182, 157, 248, 255);
        f15987x = j0.d(208, TsExtractor.TS_PACKET_SIZE, 255, 255);
        f15988y = j0.d(WinError.ERROR_MORE_DATA, 221, 255, 255);
        j0.d(246, 237, 255, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        f15989z = j0.d(29, 25, 43, 255);
        A = j0.d(255, 255, 255, 255);
        j0.d(51, 45, 65, 255);
        B = j0.d(74, 68, 88, 255);
        C = j0.d(98, 91, PermissionHelpers.REQUEST_MIC, 255);
        j0.d(122, PermissionHelpers.REQUEST_OVERLAY, 137, 255);
        j0.d(149, 141, 165, 255);
        j0.d(176, 167, PsExtractor.AUDIO_STREAM, 255);
        D = j0.d(204, 194, 220, 255);
        E = j0.d(WinError.ERROR_NO_DATA, 222, 248, 255);
        j0.d(246, 237, 255, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        F = j0.d(49, 17, 29, 255);
        G = j0.d(255, 255, 255, 255);
        j0.d(73, 37, 50, 255);
        H = j0.d(99, 59, 72, 255);
        I = j0.d(125, 82, 96, 255);
        j0.d(152, 105, 119, 255);
        j0.d(181, 131, 146, 255);
        j0.d(210, 157, TsExtractor.TS_STREAM_TYPE_AC4, 255);
        f15964J = j0.d(239, 184, 200, 255);
        K = j0.d(255, 216, 228, 255);
        j0.d(255, 236, 241, 255);
        j0.d(255, 251, SmbConstants.DEFAULT_SSN_LIMIT, 255);
        j0.d(255, 255, 255, 255);
    }
}
