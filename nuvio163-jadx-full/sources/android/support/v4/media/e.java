package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Process;
import android.support.v4.media.session.MediaSessionCompat$Token;

/* JADX INFO: loaded from: classes3.dex */
public class e implements c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f1890a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final MediaBrowser f1891b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Bundle f1892c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final a f1893d = new a(this);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final t.f f1894e = new t.f((Object) null);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public io.sentry.internal.debugmeta.c f1895f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public Messenger f1896g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public MediaSessionCompat$Token f1897h;

    public e(Context context, ComponentName componentName, d dVar) {
        this.f1890a = context;
        Bundle bundle = new Bundle();
        this.f1892c = bundle;
        bundle.putInt("extra_client_version", 1);
        bundle.putInt("extra_calling_pid", Process.myPid());
        dVar.setInternalConnectionCallback(this);
        this.f1891b = new MediaBrowser(context, componentName, dVar.mConnectionCallbackFwk, bundle);
    }
}
