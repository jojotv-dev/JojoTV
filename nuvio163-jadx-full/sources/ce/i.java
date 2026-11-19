package ce;

import androidx.media3.common.C;
import java.nio.ByteBuffer;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f6841a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public ByteBuffer f6842b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ByteBuffer f6843c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f6844d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public xd.b f6845e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public long f6846f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public long f6847g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public long f6848h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f6849i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public long f6850j;
    public long k;

    public i(int i10, ByteBuffer atlasBuf, ByteBuffer vertexBuf, xd.b frame) {
        Intrinsics.checkNotNullParameter(atlasBuf, "atlasBuf");
        Intrinsics.checkNotNullParameter(vertexBuf, "vertexBuf");
        Intrinsics.checkNotNullParameter(frame, "frame");
        this.f6841a = i10;
        this.f6842b = atlasBuf;
        this.f6843c = vertexBuf;
        this.f6844d = 1;
        this.f6845e = frame;
        this.f6846f = 0L;
        this.f6847g = C.TIME_UNSET;
        this.f6848h = 0L;
        this.f6849i = 0L;
        this.f6850j = 0L;
        this.k = 0L;
    }
}
