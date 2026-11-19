package io.sentry.android.core;

import java.io.File;
import java.util.HashMap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f24958a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f24959b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f24960c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Object f24961d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f24962e;

    public t(long j10, long j11, String headline, String str, boolean z2) {
        Intrinsics.checkNotNullParameter(headline, "headline");
        this.f24961d = headline;
        this.f24958a = j10;
        this.f24959b = j11;
        this.f24962e = str;
        this.f24960c = z2;
    }

    public t(long j10, long j11, boolean z2, File file, HashMap map) {
        this.f24958a = j10;
        this.f24961d = file;
        this.f24959b = j11;
        this.f24962e = map;
        this.f24960c = z2;
    }
}
