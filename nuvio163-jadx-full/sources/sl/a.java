package sl;

import androidx.media3.exoplayer.analytics.AnalyticsListener;
import com.liskovsoft.sharedutils.helpers.PermissionHelpers;
import h1.f;
import k4.t;
import okhttp3.internal.http.HttpStatusCodesKt;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f56877g = new a("HAETAE-2", 2, 4, 1474, 480, 96, new t[]{new t(0, 382), new t(382, 246), new t(628, 65), new t(693, 7), new t(700, 1), new t(701, 1), new t(702, 1), new t(703, 1), new t(704, 1), new t(705, 1), new t(706, 7), new t(713, 65), new t(778, 246)}, new t[]{new t(0, 1), new t(1, 1), new t(2, 1), new t(3, 5), new t(8, 58), new t(66, 246), new t(312, 398), new t(710, 247), new t(957, 59), new t(AnalyticsListener.EVENT_VIDEO_DECODER_INITIALIZED, 5), new t(AnalyticsListener.EVENT_VIDEO_FRAME_PROCESSING_OFFSET, 1), new t(AnalyticsListener.EVENT_DRM_SESSION_ACQUIRED, 1), new t(1023, 1)});

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final a f56878h = new a("HAETAE-3", 3, 6, 2349, 480, 96, new t[]{new t(0, 266), new t(266, 213), new t(479, PermissionHelpers.REQUEST_MIC), new t(592, 40), new t(632, 9), new t(641, 1), new t(642, 1), new t(643, 1), new t(644, 1), new t(645, 1), new t(646, 1), new t(647, 1), new t(648, 1), new t(649, 9), new t(658, 40), new t(698, PermissionHelpers.REQUEST_MIC), new t(811, 213)}, new t[]{new t(0, 1), new t(1, 1), new t(2, 1), new t(3, 1), new t(4, 8), new t(12, 37), new t(49, 112), new t(161, 216), new t(377, 269), new t(646, 216), new t(862, 112), new t(974, 38), new t(AnalyticsListener.EVENT_AUDIO_DECODER_RELEASED, 8), new t(AnalyticsListener.EVENT_VIDEO_DISABLED, 1), new t(AnalyticsListener.EVENT_VIDEO_FRAME_PROCESSING_OFFSET, 1), new t(AnalyticsListener.EVENT_DRM_SESSION_ACQUIRED, 1), new t(1023, 1)});

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f56879i = new a("HAETAE-5", 4, 7, 2948, 512, 64, new t[]{new t(0, 122), new t(122, 117), new t(239, HttpStatusCodesKt.HTTP_PROCESSING), new t(341, 81), new t(422, 59), new t(481, 39), new t(520, 23), new t(543, 13), new t(556, 6), new t(562, 3), new t(565, 1), new t(566, 1), new t(567, 1), new t(568, 1), new t(569, 1), new t(570, 1), new t(571, 1), new t(572, 1), new t(573, 1), new t(574, 1), new t(575, 1), new t(576, 1), new t(577, 1), new t(578, 1), new t(579, 3), new t(582, 6), new t(588, 13), new t(601, 24), new t(625, 39), new t(664, 59), new t(723, 81), new t(804, HttpStatusCodesKt.HTTP_PROCESSING), new t(906, 118)}, new t[]{new t(0, 1), new t(1, 1), new t(2, 1), new t(3, 1), new t(4, 2), new t(6, 13), new t(19, 48), new t(67, 118), new t(185, 204), new t(389, 245), new t(634, 204), new t(838, 118), new t(956, 48), new t(1004, 14), new t(AnalyticsListener.EVENT_DROPPED_VIDEO_FRAMES, 2), new t(AnalyticsListener.EVENT_VIDEO_DISABLED, 1), new t(AnalyticsListener.EVENT_VIDEO_FRAME_PROCESSING_OFFSET, 1), new t(AnalyticsListener.EVENT_DRM_SESSION_ACQUIRED, 1), new t(1023, 1)});

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f56880a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f56881b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f56882c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f56883d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f56884e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f56885f;

    public a(String str, int i10, int i11, int i12, int i13, int i14, t[] tVarArr, t[] tVarArr2) {
        this.f56880a = str;
        this.f56881b = i10;
        this.f56882c = i11;
        this.f56883d = i12;
        a(tVarArr);
        a(tVarArr2);
        int i15 = (i13 * i10) + 32;
        this.f56884e = i15;
        this.f56885f = (i10 * i14) + ((i11 - 1) * 64) + i15 + 32;
    }

    public static void a(t[] tVarArr) {
        short[] sArr = new short[1024];
        for (int i10 = 0; i10 < tVarArr.length; i10++) {
            t tVar = tVarArr[i10];
            int i11 = tVar.f27841a & 65535;
            int i12 = tVar.f27842b & 65535;
            for (int i13 = 0; i13 < i12; i13++) {
                sArr[i11 + i13] = (short) i10;
            }
        }
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.f56880a);
        sb2.append(" (K=");
        sb2.append(this.f56881b);
        sb2.append(", L=");
        sb2.append(this.f56882c);
        sb2.append(", signature=");
        return f.j(this.f56883d, " bytes)", sb2);
    }
}
