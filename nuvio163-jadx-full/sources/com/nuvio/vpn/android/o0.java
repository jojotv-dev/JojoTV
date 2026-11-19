package com.nuvio.vpn.android;

import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes4.dex */
public final class o0 implements AutoCloseable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Context f10949b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HttpStreamTunnel f10950c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final AtomicBoolean f10951e = new AtomicBoolean();

    public o0(Context context, HttpStreamTunnel httpStreamTunnel) {
        this.f10949b = context;
        this.f10950c = httpStreamTunnel;
    }

    @Override // java.lang.AutoCloseable
    public final void close() {
        if (this.f10951e.compareAndSet(false, true)) {
            r0.f10975a.execute(new n0(this, 0));
        }
    }

    public final String g() {
        StringBuilder sb2 = new StringBuilder("http://nuvio:");
        HttpStreamTunnel httpStreamTunnel = this.f10950c;
        sb2.append(httpStreamTunnel.f10772i);
        sb2.append("@127.0.0.1:");
        sb2.append(httpStreamTunnel.f10771e);
        return sb2.toString();
    }
}
