package tc;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f57314a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f57315b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f57316c;

    public d(String decoded, List tokens, boolean z2) {
        Intrinsics.checkNotNullParameter(decoded, "decoded");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        this.f57314a = decoded;
        this.f57315b = tokens;
        this.f57316c = z2;
    }
}
