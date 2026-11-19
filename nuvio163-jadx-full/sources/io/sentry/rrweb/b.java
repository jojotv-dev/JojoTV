package io.sentry.rrweb;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public abstract class b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public c f26116b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f26117c = System.currentTimeMillis();

    public b(c cVar) {
        this.f26116b = cVar;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return this.f26117c == bVar.f26117c && this.f26116b == bVar.f26116b;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.f26116b, Long.valueOf(this.f26117c)});
    }
}
