package io.sentry.android.replay.viewhierarchy;

import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public abstract class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f25339a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f25340b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f25341c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f25342d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f25343e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final Rect f25344f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public ArrayList f25345g;

    public g(int i10, int i11, float f10, g gVar, boolean z2, boolean z10, Rect rect) {
        this.f25339a = i10;
        this.f25340b = i11;
        this.f25341c = f10;
        this.f25342d = z2;
        this.f25343e = z10;
        this.f25344f = rect;
    }

    public final void a(io.sentry.android.replay.util.d callback) {
        ArrayList arrayList;
        Intrinsics.checkNotNullParameter(callback, "callback");
        if (!((Boolean) callback.invoke(this)).booleanValue() || (arrayList = this.f25345g) == null) {
            return;
        }
        Intrinsics.checkNotNull(arrayList);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((g) it.next()).a(callback);
        }
    }
}
