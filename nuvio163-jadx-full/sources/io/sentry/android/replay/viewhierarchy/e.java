package io.sentry.android.replay.viewhierarchy;

import android.graphics.Rect;
import java.lang.ref.WeakReference;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class e extends g {

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final WeakReference f25335h;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public e(WeakReference surfaceViewRef, int i10, int i11, float f10, g gVar, boolean z2, boolean z10, Rect rect) {
        super(i10, i11, f10, gVar, z2, z10, rect);
        Intrinsics.checkNotNullParameter(surfaceViewRef, "surfaceViewRef");
        this.f25335h = surfaceViewRef;
    }
}
