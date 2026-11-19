package com.nuvio.tv.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class n2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final SharedPreferences f9062a;

    public n2(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.f9062a = context.getSharedPreferences("image_performance", 0);
    }

    public final boolean a() {
        return this.f9062a.getBoolean("rgb565_enabled", true);
    }
}
