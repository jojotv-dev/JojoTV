package z5;

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

/* JADX INFO: loaded from: classes.dex */
public abstract class e {
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
    public static final long f65573J;
    public static final long K;
    public static final long L;
    public static final long M;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final long f65574a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f65575b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final long f65576c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final long f65577d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final long f65578e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final long f65579f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final long f65580g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final long f65581h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final long f65582i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final long f65583j;
    public static final long k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final long f65584l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final long f65585m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final long f65586n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final long f65587o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final long f65588p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final long f65589q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final long f65590r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final long f65591s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final long f65592t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final long f65593u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public static final long f65594v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public static final long f65595w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public static final long f65596x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final long f65597y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final long f65598z;

    static {
        j0.d(0, 0, 0, 255);
        j0.d(0, 0, 0, 255);
        f65574a = j0.d(65, 14, 11, 255);
        f65575b = j0.d(255, 255, 255, 255);
        f65576c = j0.d(96, 20, 16, 255);
        f65577d = j0.d(140, 29, 24, 255);
        f65578e = j0.d(179, 38, 30, 255);
        j0.d(220, 54, 46, 255);
        j0.d(228, 105, 98, 255);
        j0.d(236, 146, 142, 255);
        f65579f = j0.d(242, 184, 181, 255);
        f65580g = j0.d(249, 222, 220, 255);
        j0.d(252, 238, 238, 255);
        j0.d(255, 251, 249, 255);
        f65581h = j0.d(0, 0, 0, 255);
        f65582i = j0.d(28, 27, 31, 255);
        j0.d(255, 255, 255, 255);
        f65583j = j0.d(49, 48, 51, 255);
        j0.d(72, 70, 73, 255);
        j0.d(96, 93, 98, 255);
        j0.d(120, 117, 121, 255);
        j0.d(147, 144, 148, 255);
        j0.d(174, 170, 174, 255);
        j0.d(201, 197, 202, 255);
        k = j0.d(WinError.ERROR_BAD_PIPE, 225, 229, 255);
        f65584l = j0.d(244, 239, 244, 255);
        f65585m = j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        j0.d(29, 26, 34, 255);
        j0.d(255, 255, 255, 255);
        j0.d(50, 47, 55, 255);
        f65586n = j0.d(73, 69, 79, 255);
        j0.d(96, 93, HttpStatusCodesKt.HTTP_PROCESSING, 255);
        f65587o = j0.d(121, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, WebSocketProtocol.PAYLOAD_SHORT, 255);
        f65588p = j0.d(147, NbtException.UNSPECIFIED, 153, 255);
        j0.d(174, 169, 180, 255);
        f65589q = j0.d(202, 196, 208, 255);
        f65590r = j0.d(WinError.ERROR_PIPE_BUSY, 224, 236, 255);
        j0.d(245, 238, SmbConstants.DEFAULT_SSN_LIMIT, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        f65591s = j0.d(33, 0, 93, 255);
        f65592t = j0.d(255, 255, 255, 255);
        f65593u = j0.d(56, 30, PermissionHelpers.REQUEST_OVERLAY, 255);
        f65594v = j0.d(79, 55, TsExtractor.TS_STREAM_TYPE_DTS_UHD, 255);
        f65595w = j0.d(HttpStatusCodesKt.HTTP_EARLY_HINTS, 80, 164, 255);
        j0.d(127, HttpStatusCodesKt.HTTP_EARLY_HINTS, 190, 255);
        j0.d(154, 130, 219, 255);
        j0.d(182, 157, 248, 255);
        f65596x = j0.d(208, TsExtractor.TS_PACKET_SIZE, 255, 255);
        f65597y = j0.d(WinError.ERROR_MORE_DATA, 221, 255, 255);
        j0.d(246, 237, 255, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        f65598z = j0.d(29, 25, 43, 255);
        A = j0.d(255, 255, 255, 255);
        B = j0.d(51, 45, 65, 255);
        C = j0.d(74, 68, 88, 255);
        D = j0.d(98, 91, PermissionHelpers.REQUEST_MIC, 255);
        j0.d(122, PermissionHelpers.REQUEST_OVERLAY, 137, 255);
        j0.d(149, 141, 165, 255);
        j0.d(176, 167, PsExtractor.AUDIO_STREAM, 255);
        E = j0.d(204, 194, 220, 255);
        F = j0.d(WinError.ERROR_NO_DATA, 222, 248, 255);
        j0.d(246, 237, 255, 255);
        j0.d(255, 251, 254, 255);
        j0.d(0, 0, 0, 255);
        G = j0.d(49, 17, 29, 255);
        H = j0.d(255, 255, 255, 255);
        I = j0.d(73, 37, 50, 255);
        f65573J = j0.d(99, 59, 72, 255);
        K = j0.d(125, 82, 96, 255);
        j0.d(152, 105, 119, 255);
        j0.d(181, 131, 146, 255);
        j0.d(210, 157, TsExtractor.TS_STREAM_TYPE_AC4, 255);
        L = j0.d(239, 184, 200, 255);
        M = j0.d(255, 216, 228, 255);
        j0.d(255, 236, 241, 255);
        j0.d(255, 251, SmbConstants.DEFAULT_SSN_LIMIT, 255);
        j0.d(255, 255, 255, 255);
    }
}
