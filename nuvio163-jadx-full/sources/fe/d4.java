package fe;

import java.util.LinkedHashMap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d4 extends RuntimeException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedHashMap f16715b;

    public d4(LinkedHashMap channels) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        this.f16715b = channels;
    }
}
