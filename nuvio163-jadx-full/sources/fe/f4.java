package fe;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f16839a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f16840b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f16841c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Map f16842d;

    public f4(String str, long j10, long j11, Map window) {
        Intrinsics.checkNotNullParameter(window, "window");
        this.f16839a = str;
        this.f16840b = j10;
        this.f16841c = j11;
        this.f16842d = window;
    }
}
