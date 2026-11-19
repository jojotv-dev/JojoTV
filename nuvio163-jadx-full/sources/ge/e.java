package ge;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f19214a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f19215b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Integer f19216c;

    public e(String text, int i10, int i11) {
        Integer num = (i11 & 8) != 0 ? null : -50384;
        Intrinsics.checkNotNullParameter(text, "text");
        this.f19214a = text;
        this.f19215b = i10;
        this.f19216c = num;
    }

    public final String toString() {
        return this.f19214a;
    }
}
