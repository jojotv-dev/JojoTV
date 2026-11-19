package jd;

import java.io.Closeable;
import java.util.List;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f implements Closeable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function4 f27296b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f27297c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f27298e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f27299i;

    public f(Function4 readAt, List resources, long j10) {
        Intrinsics.checkNotNullParameter(readAt, "readAt");
        Intrinsics.checkNotNullParameter(resources, "resources");
        this.f27296b = readAt;
        this.f27297c = resources;
        this.f27298e = j10;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        for (AutoCloseable autoCloseable : this.f27297c) {
            try {
                Result.Companion companion = Result.INSTANCE;
                h1.f.t(autoCloseable);
                Result.m293constructorimpl(Unit.INSTANCE);
            } catch (Throwable th2) {
                Result.Companion companion2 = Result.INSTANCE;
                Result.m293constructorimpl(ResultKt.createFailure(th2));
            }
        }
    }

    public final synchronized int read(byte[] buffer, int i10, int i11) {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        if (i11 <= 0) {
            return 0;
        }
        int iIntValue = ((Number) this.f27296b.invoke(Long.valueOf(this.f27299i), buffer, Integer.valueOf(i10), Integer.valueOf(i11))).intValue();
        if (iIntValue > 0) {
            this.f27299i += (long) iIntValue;
        }
        return iIntValue;
    }

    public final synchronized void seek(long j10) {
        try {
            if (j10 < 0) {
                throw new IllegalArgumentException("Position SMB invalide.");
            }
            this.f27299i = j10;
        } catch (Throwable th2) {
            throw th2;
        }
    }
}
