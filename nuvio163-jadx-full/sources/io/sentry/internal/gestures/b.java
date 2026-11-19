package io.sentry.internal.gestures;

import android.view.View;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final WeakReference f25627a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f25628b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f25629c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f25630d = "old_view_system";

    public b(View view, String str, String str2) {
        this.f25627a = new WeakReference(view);
        this.f25628b = str;
        this.f25629c = str2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || b.class != obj.getClass()) {
            return false;
        }
        b bVar = (b) obj;
        return io.sentry.util.b.g(this.f25628b, bVar.f25628b) && io.sentry.util.b.g(this.f25629c, bVar.f25629c) && io.sentry.util.b.g(null, null);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f25627a, this.f25629c, null});
    }
}
