package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.d2;
import io.sentry.i3;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes5.dex */
public final class j implements d2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f25923b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public ConcurrentHashMap f25924c;

    public j(List list) {
        this.f25923b = list;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || j.class != obj.getClass()) {
            return false;
        }
        return io.sentry.util.b.g(this.f25923b, ((j) obj).f25923b);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f25923b});
    }

    @Override // io.sentry.d2
    public final void serialize(i3 i3Var, ILogger iLogger) {
        v0.m mVar = (v0.m) i3Var;
        mVar.u();
        mVar.F("values");
        mVar.T(iLogger, this.f25923b);
        ConcurrentHashMap concurrentHashMap = this.f25924c;
        if (concurrentHashMap != null) {
            for (String str : concurrentHashMap.keySet()) {
                h1.f.y(this.f25924c, str, mVar, str, iLogger);
            }
        }
        mVar.w();
    }
}
