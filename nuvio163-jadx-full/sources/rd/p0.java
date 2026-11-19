package rd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p0 extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f52460b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f52461c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final String f52462e;

    public /* synthetic */ p0(Integer num, String str, String str2) {
        this(num, str, str2, null);
    }

    public final String a() {
        return this.f52461c;
    }

    public final Integer b() {
        return this.f52460b;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        return this.f52462e;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public p0(Integer num, String str, String message, Throwable th2) {
        super(message, th2);
        Intrinsics.checkNotNullParameter(message, "message");
        this.f52460b = num;
        this.f52461c = str;
        this.f52462e = message;
    }
}
