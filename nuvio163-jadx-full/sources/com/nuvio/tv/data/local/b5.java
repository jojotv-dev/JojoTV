package com.nuvio.tv.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final SharedPreferences f7980a;

    public b5(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.f7980a = context.getSharedPreferences("member_catalogs", 0);
    }

    public final String a() {
        return this.f7980a.getString("avatar_catalog", null);
    }

    public final void b(String payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        this.f7980a.edit().putString("avatar_catalog", payload).apply();
    }
}
